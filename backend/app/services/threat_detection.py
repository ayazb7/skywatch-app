import ollama
import re
from typing import Tuple
from pathlib import Path


def detect_threat(image_path: str) -> Tuple[bool, str, str]:
    """
    Analyze an image for potential threats using Ollama's vision model.

    Args:
        image_path: Path to the image file to analyze

    Returns:
        A tuple of (is_threat, confidence_level, explanation) where:
        - is_threat: True if a threat is detected, False otherwise
        - confidence_level: String indicating confidence (High, Medium, Low, or Unknown)
        - explanation: String explanation of the analysis
    """
    # Check if image exists
    if not Path(image_path).exists():
        return False, "Unknown", "Image not found"

    try:
        res = ollama.chat(
        model='qwen3.5',
        messages=[{
            'role': 'user',
            'content': (
                "You are a sophisticated threat detection system analyzing video footage from a video doorbell. Your primary objective is to determine if an individual approaching the front door poses a THREAT to the resident. Your analysis must be binary: THREAT or NO THREAT. You will also assign a confidence level to your assessment."
                "Instructions:"
                "Binary Threat Assessment:"
                "THREAT: Indicates a situation where the individual's behavior, appearance, or proximity poses a credible risk of harm."
                "NO THREAT: Indicates the individual presents no immediate or obvious risk."
                "Visual Cues (Secondary – Adjust Weight Based on Analysis):"
                "Weapon Detection: Immediately scan for visible weapons. (This remains a priority if observed)"
                "Body Language Analysis: Assess posture, gestures, and eye contact, looking for signs of aggression or suspicious behavior."
                "Facial Expressions: Evaluate expressions for hostility or discomfort."
                "Clothing & Accessories: Note unusual attire."
                "Contextual Analysis (Crucial – Adjust Weight Based on Analysis):"
                "Visitor Type: Immediately categorize the individual. Options include:"
                "Construction Worker: (Assume no threat – justified by proximity to door and job function)"
                "Postman/Woman: (Assume no threat – justified by proximity to door and job function)"
                "Unknown: (Proceed with standard threat assessment)"
                "Proximity to the Door: How close is the individual? (Near = Higher Concern)"
                "Time of Day: (Consider time of day context – late night = higher concern)"
                "Output Requirements:"
                "Classification: THREAT/NO THREAT"
                "Confidence Level: (High, Medium, Low) – Assess the certainty of your classification based on the available evidence."
                "Justification (Brief – 1-2 sentences): Briefly explain the reasoning behind your classification – highlighting key visual cues and/or contextual factors."
                "Example Output (for a hypothetical image): "
                "Classification: NO THREAT"
                "Confidence Level: High"
                "Justification: The individual is clearly a postal worker, standing close to the door as part of their job. No suspicious behavior observed."
                "Important Notes for the Agent: Prioritize the visitor type classification – if a visitor is identified as a construction worker or postman/woman, the threat assessment should be automatically set to ‘No Threat’, unless other concerning factors are present. Always exercise caution when assessing unfamiliar individuals."
            ),
            'images': [image_path],
        }],
    )

        response_text = res['message']['content']

        # Parse the response to determine if threat was detected
        is_threat = 'THREAT' in response_text.upper() and 'NO THREAT' not in response_text.upper()

        # Extract confidence level from response
        confidence_match = re.search(r'Confidence Level:\s*(High|Medium|Low)', response_text, re.IGNORECASE)
        confidence_level = confidence_match.group(1) if confidence_match else "Unknown"

        # Extract justification from response
        justification_match = re.search(r'Justification:\s*(.*)', response_text, re.IGNORECASE)
        justification = justification_match.group(1) if justification_match else "Unknown"

        return is_threat, confidence_level, justification

    except Exception as e:
        return False, "Unknown", f"Error analyzing image: {str(e)}"


if __name__ == '__main__':
    # Test the function
    threat_detected, confidence, response_text = detect_threat('app/services/image.png')
    print(f"Threat detected: {threat_detected}")
    print(f"Confidence Level: {confidence}")
    print(f"Explanation: {response_text}")
