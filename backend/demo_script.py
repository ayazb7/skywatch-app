#!/usr/bin/env python3
"""
SkyWatch MVP Demo Script
========================
Interactive demo that walks through each AI feature step-by-step.

Usage:
    python demo_script.py

Prerequisites:
    - Backend running at http://localhost:8000
    - Ollama running with llava:7b model (for threat detection)
    - Demo images in ./demo_images/
    - A photo of yourself as doorbell_familiar.jpg
      (this should match the face you manually add in the app)

The script pauses between each step so you can narrate and show the app.
"""

import requests
import time
import sys
import os
from datetime import date

BASE_URL = "http://localhost:8000"
DEMO_IMAGES_DIR = os.path.join(os.path.dirname(__file__), "demo_images")

# ANSI colours for terminal output
GREEN = "\033[92m"
BLUE = "\033[94m"
RED = "\033[91m"
YELLOW = "\033[93m"
BOLD = "\033[1m"
RESET = "\033[0m"


def banner(text: str, color: str = BLUE):
    width = max(len(text) + 4, 50)
    print(f"\n{color}{BOLD}{'═' * width}")
    print(f"  {text}")
    print(f"{'═' * width}{RESET}\n")


def step_info(step_num: int, title: str, description: str):
    print(f"{GREEN}{BOLD}[Step {step_num}]{RESET} {BOLD}{title}{RESET}")
    print(f"  {description}\n")


def pause(message: str = "Press Enter to continue to the next step..."):
    print(f"{YELLOW}{BOLD}    ⏸  {message}{RESET}")
    input()


def check_backend():
    """Verify the backend is running."""
    try:
        r = requests.get(f"{BASE_URL}/api/health")
        if r.status_code == 200:
            print(f"{GREEN}✓ Backend is running{RESET}")
            return True
    except requests.ConnectionError:
        pass
    print(f"{RED}✗ Backend is not running at {BASE_URL}{RESET}")
    print(f"  Start it with: PYTHONPATH=. uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload")
    return False


def check_demo_images():
    """Check which demo images are available."""
    required = {
        "doorbell_familiar.jpg": "Your face at a door (for recognition test)",
        "doorbell_threat.png": "Suspicious person at door",
        "doorbell_normal.png": "Normal/empty porch with motion",
        "doorbell_package.png": "Package on porch",
    }
    
    all_found = True
    for filename, desc in required.items():
        path = os.path.join(DEMO_IMAGES_DIR, filename)
        if os.path.exists(path):
            print(f"  {GREEN}✓{RESET} {filename} — {desc}")
        else:
            print(f"  {RED}✗{RESET} {filename} — {desc} {RED}(MISSING){RESET}")
            all_found = False
    
    return all_found


def clean_events():
    """Fetch and delete all events for today."""
    today = date.today().isoformat()
    r = requests.get(f"{BASE_URL}/api/events", params={"date": today, "limit": 100})
    if r.status_code == 200:
        events = r.json()
        for event in events:
            requests.delete(f"{BASE_URL}/api/events/{event['id']}")
        print(f"  Cleared {len(events)} existing events for {today}")


def clean_faces():
    """Delete all familiar faces."""
    r = requests.get(f"{BASE_URL}/api/faces")
    if r.status_code == 200:
        faces = r.json()
        for face in faces:
            requests.delete(f"{BASE_URL}/api/faces/{face['id']}")
        print(f"  Cleared {len(faces)} existing familiar faces")


def step_1_familiar_face_recognition():
    """Send a photo to the doorbell endpoint to trigger face recognition."""
    image_path = os.path.join(DEMO_IMAGES_DIR, "doorbell_familiar.jpg")
    
    print(f"  Sending image to doorbell AI pipeline...")
    with open(image_path, "rb") as img:
        r = requests.post(
            f"{BASE_URL}/api/doorbell/upload",
            files={"file": ("doorbell_capture.jpg", img, "image/jpeg")}
        )
    
    if r.status_code == 200:
        result = r.json()
        if result.get("is_familiar"):
            print(f"  {GREEN}✓ Face recognised: {result.get('matched_name')}{RESET}")
            print(f"  Event type: {result.get('event_type')}")
            print(f"  Summary: {result.get('summary')}")
        else:
            print(f"  {YELLOW}⚠ Face not recognised (DeepFace did not match){RESET}")
            print(f"  Event type: {result.get('event_type')}")
            print(f"  Summary: {result.get('summary')}")
        return result
    else:
        print(f"  {RED}✗ Doorbell endpoint failed: {r.text}{RESET}")
        return None


def step_2_threat_detection():
    """Send a suspicious image to trigger threat detection."""
    image_path = os.path.join(DEMO_IMAGES_DIR, "doorbell_threat.png")
    
    print(f"  Sending suspicious image to doorbell AI pipeline...")
    print(f"  {YELLOW}    (This may take 10-30 seconds — Ollama is analysing the image){RESET}")
    
    with open(image_path, "rb") as img:
        r = requests.post(
            f"{BASE_URL}/api/doorbell/upload",
            files={"file": ("doorbell_capture.jpg", img, "image/jpeg")}
        )
    
    if r.status_code == 200:
        result = r.json()
        if result.get("is_threat"):
            print(f"  {RED}⚠ THREAT DETECTED!{RESET}")
            print(f"  Confidence: {result.get('threat_confidence')}")
            print(f"  Explanation: {result.get('threat_explanation')}")
        else:
            print(f"  {GREEN}✓ No threat detected (Ollama classified as safe){RESET}")
            print(f"  Event type: {result.get('event_type')}")
        return result
    else:
        print(f"  {RED}✗ Doorbell endpoint failed: {r.text}{RESET}")
        return None


def step_3_motion_detection():
    """Send a normal image to trigger a motion event."""
    image_path = os.path.join(DEMO_IMAGES_DIR, "doorbell_normal.png")
    
    print(f"  Sending normal porch image to doorbell AI pipeline...")
    print(f"  {YELLOW}    (This may take 10-30 seconds — Ollama is analysing the image){RESET}")
    
    with open(image_path, "rb") as img:
        r = requests.post(
            f"{BASE_URL}/api/doorbell/upload",
            files={"file": ("doorbell_capture.jpg", img, "image/jpeg")}
        )
    
    if r.status_code == 200:
        result = r.json()
        print(f"  {GREEN}✓ Event created:{RESET} {result.get('event_type')}")
        print(f"  Summary: {result.get('summary')}")
        return result
    else:
        print(f"  {RED}✗ Doorbell endpoint failed: {r.text}{RESET}")
        return None


def step_4_package_detection():
    """Trigger a package detection event via demo endpoint with image."""
    image_path = os.path.join(DEMO_IMAGES_DIR, "doorbell_package.png")
    
    with open(image_path, "rb") as img:
        r = requests.post(
            f"{BASE_URL}/api/demo/trigger",
            data={
                "type": "PACKAGE",
                "description": "Package delivered at front door",
                "is_threat": "false",
                "threat_confidence": "LOW",
            },
            files={"image": ("package.png", img, "image/png")}
        )
    
    if r.status_code == 201:
        result = r.json()
        print(f"  {GREEN}✓ Package event created (ID: {result.get('id')}){RESET}")
        return result
    else:
        print(f"  {RED}✗ Demo trigger failed: {r.text}{RESET}")
        return None


def main():
    banner("SkyWatch MVP Demo Script")
    
    # Pre-flight checks
    print(f"{BOLD}Pre-flight checks:{RESET}")
    if not check_backend():
        sys.exit(1)
    
    print(f"\n{BOLD}Demo images:{RESET}")
    images_ok = check_demo_images()
    
    if not images_ok:
        print(f"\n{YELLOW}    ⚠ Some demo images are missing.{RESET}")
        print(f"      Place them in: {DEMO_IMAGES_DIR}")
        pause("Press Enter to continue anyway (missing steps will be skipped)...")
    
    # Clean slate
    print(f"\n{BOLD}Preparing clean slate...{RESET}")
    clean_events()
    clean_faces() # Restored as requested
    
    print(f"\n{YELLOW}🎬 PROMPT: Open the app and manually add a Familiar Face first.{RESET}")
    pause("Once the face is added in the app, press Enter to begin the automated recognition steps...")
    
    # ─── Step 1: Familiar Face Recognition ────────────────────────
    banner("Step 1: Familiar Face Recognition (DeepFace)", BLUE)
    step_info(1, "AI Face Recognition",
              "A 'doorbell frame' with the familiar person arrives.\n"
              "  DeepFace compares it against registered faces → match found → PERSON_DETECTED event.")
    
    doorbell_familiar = os.path.join(DEMO_IMAGES_DIR, "doorbell_familiar.jpg")
    if os.path.exists(doorbell_familiar):
        step_1_familiar_face_recognition()
        print()
        pause("📱 Show the app: Watch the timeline update live with 'Person Detected' event.\n"
              "       Tap the event to show the detail sheet with the matched face, then press Enter...")
    else:
        print(f"  {YELLOW}⏭ Skipping (doorbell_familiar.jpg not found){RESET}")
        pause()
    
    # ─── Step 2: Threat Detection ─────────────────────────────────
    banner("Step 2: Threat Detection (Ollama Vision AI)", RED)
    step_info(2, "AI Threat Detection",
              "A 'doorbell frame' with an unfamiliar, suspicious person arrives.\n"
              "  DeepFace finds no match → Ollama vision model analyses the image for threats.")
    
    threat_image = os.path.join(DEMO_IMAGES_DIR, "doorbell_threat.png")
    if os.path.exists(threat_image):
        step_2_threat_detection()
        print()
        pause("📱 Show the app: Watch for THREAT ALERT banner on the video feed.\n"
              "       Tap the threat event to show AI analysis details, then press Enter...")
    else:
        print(f"  {YELLOW}⏭ Skipping (doorbell_threat.png not found){RESET}")
        pause()
    
    # ─── Step 3: Normal Motion ────────────────────────────────────
    banner("Step 3: Normal Motion Detection", BLUE)
    step_info(3, "Motion Event",
              "A normal 'doorbell frame' arrives — no face match, no threat.\n"
              "  The system logs it as a standard motion event.")
    
    normal_image = os.path.join(DEMO_IMAGES_DIR, "doorbell_normal.png")
    if os.path.exists(normal_image):
        step_3_motion_detection()
        print()
        pause("📱 Show the app: Motion Detected event appears in timeline, then press Enter...")
    else:
        print(f"  {YELLOW}⏭ Skipping (doorbell_normal.png not found){RESET}")
        pause()
    
    # ─── Step 4: Package Detection ────────────────────────────────
    banner("Step 4: Package Detection", GREEN)
    step_info(4, "Package Event",
              "A package delivery is detected at the front door.\n"
              "  This uses the demo trigger endpoint with an attached image.")
    
    package_image = os.path.join(DEMO_IMAGES_DIR, "doorbell_package.png")
    if os.path.exists(package_image):
        step_4_package_detection()
        print()
        pause("📱 Show the app: Package Detected event appears with screenshot, then press Enter...")
    else:
        print(f"  {YELLOW}⏭ Skipping (doorbell_package.png not found){RESET}")
        pause()
    
    # ─── Finale ───────────────────────────────────────────────────
    banner("Demo Complete! 🎉", GREEN)
    print(f"  {BOLD}Final shot:{RESET} Show the full timeline with all event types.")
    print(f"  Navigate between dates to show historical events.")
    print(f"  Tap individual events to show their detail sheets.")
    print()
    print(f"  {BOLD}Events created during this demo:{RESET}")
    print(f"  • PERSON_DETECTED — Familiar face recognised by DeepFace")
    print(f"  • THREAT — Suspicious person flagged by Ollama vision AI")
    print(f"  • MOTION — Normal activity logged")
    print(f"  • PACKAGE — Delivery detected")
    print()


if __name__ == "__main__":
    main()
