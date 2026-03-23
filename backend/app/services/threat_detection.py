import ollama
import re
from typing import Tuple
from pathlib import Path


class ThreatDetectionService:
    def __init__(self):
        self.model = 'llava:7b'

    def warmup(self):
        """
        Verify if the model is available in Ollama, and pull it if it's not.
        Should be called during application startup.
        """
        try:
            # Check if model exists
            response = ollama.list()
            
            models_list = []
            if hasattr(response, 'models'):
                models_list = response.models
            elif isinstance(response, dict):
                models_list = response.get('models', [])
                
            model_names = []
            for m in models_list:
                if hasattr(m, 'model'):
                    model_names.append(m.model)
                elif hasattr(m, 'name'):
                    model_names.append(m.name)
                elif isinstance(m, dict):
                    model_names.append(m.get('model') or m.get('name'))

            if self.model not in model_names and f"{self.model}:latest" not in model_names:
                print(f"Ollama model {self.model} not found. Downloading it...")
                ollama.pull(self.model)
                print(f"Model {self.model} downloaded successfully.")
            else:
                pass 
        except Exception as e:
            print(f"Warning: Failed to warmup threat detection model: {e}")

    async def detect_threat(self, image_path: str) -> Tuple[bool, str, str]:
        """
        Analyze an image for potential threats using Ollama's vision model.
        Uses a concise prompt and limited output tokens for fast response.
        """
        # Check if image exists
        if not Path(image_path).exists():
            return False, "UNKNOWN", "Image not found"

        try:
            res = ollama.chat(
                model=self.model,
                messages=[{
                    'role': 'user',
                    'content': (
                        "You are a home security doorbell threat detector. "
                        "Analyze this image and respond in EXACTLY this format (3 lines, nothing else):\n\n"
                        "Classification: THREAT\n"
                        "Confidence Level: High\n"
                        "Justification: One sentence.\n\n"
                        "RULES — classify as THREAT if ANY of these apply:\n"
                        "- Person wearing a mask, balaclava, or face covering\n"
                        "- Person holding a weapon or using tools to force entry\n"
                        "- Person attempting to break in or tamper with the door\n"
                        "- Aggressive or threatening body language\n"
                        "- Person lurking, hiding, or casing the property\n\n"
                        "Classify as NO THREAT if:\n"
                        "- No person is visible (empty porch, package delivery, animal)\n"
                        "- Person is clearly a delivery worker, postman, or courier\n"
                        "- Person is a normal visitor with no suspicious behavior\n"
                        "- Person is a child or family member\n\n"
                        "When a person with a concealed face is present, ALWAYS classify as THREAT."
                    ),
                    'images': [image_path],
                }],
                options={
                    'num_predict': 150,
                    'temperature': 0.1,
                },
            )

            response_text = res['message']['content']

            # Parse the Classification line specifically
            classification_match = re.search(
                r'Classification:\s*(THREAT|NO THREAT)', 
                response_text, re.IGNORECASE
            )
            if classification_match:
                is_threat = classification_match.group(1).strip().upper() == "THREAT"
            else:
                # Fallback: if no clear classification line, check for threat keywords
                upper_text = response_text.upper()
                is_threat = 'THREAT' in upper_text and 'NO THREAT' not in upper_text

            # Extract confidence level from response
            confidence_match = re.search(r'Confidence Level:\s*(High|Medium|Low)', response_text, re.IGNORECASE)
            confidence_level = confidence_match.group(1) if confidence_match else "Unknown"

            # Extract justification from response
            justification_match = re.search(r'Justification:\s*(.*)', response_text, re.IGNORECASE)
            justification = justification_match.group(1).strip() if justification_match else response_text.strip()

            return is_threat, confidence_level, justification

        except Exception as e:
            return False, "UNKNOWN", f"Error analyzing image: {str(e)}"

threat_detection_service = ThreatDetectionService()

if __name__ == '__main__':
    import asyncio
    # Test the function
    async def test():
        threat_detected, confidence, response_text = await threat_detection_service.detect_threat('app/services/image.png')
        print(f"Threat detected: {threat_detected}")
        print(f"Confidence Level: {confidence}")
        print(f"Explanation: {response_text}")
    
    asyncio.run(test())
