# QR Craft Studio - Project Guidelines & Monetization Rules

## Monetization Rules (MANDATORY & PERSISTED)
1. **Freemium Model**:
   - Free tier includes basic static QR codes (text, URL, Wi-Fi) with a daily limit of 3 colored/styled QR codes.
   - AdMob banner at the bottom and interstitial ad on every 3rd save in Free tier.
2. **Pro Subscription (PRO VIP Member)**:
   - Ad-free experience.
   - Unlimited customization (colors, gradients, frame templates, center logo embedding with Error Correction Level H).
   - Vector and HD exports (SVG, PDF, HD PNG).
3. **B2B Business Tools**:
   - Dynamic QR codes with editable short links.
   - Google Review Generator for local businesses.

## Architecture & Tech Stack
- **Architecture**: MVVM
- **UI Framework**: Jetpack Compose + Material Design 3 (Dark theme with light purple accents)
- **Core Libraries**: ZXing (`com.google.zxing:core`), CameraX, Room DB, Google Play Billing / RevenueCat, Google AdMob SDK.
