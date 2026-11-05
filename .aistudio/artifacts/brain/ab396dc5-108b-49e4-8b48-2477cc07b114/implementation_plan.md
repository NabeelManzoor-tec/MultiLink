# Google Play Billing Integration, Strict PRO Feature Gating & Developer Payout System

A production-grade implementation to enforce genuine Google Play In-App Billing for PRO subscriptions, strictly lock premium features against unverified access, provide developer payout instructions for bank transfers via Google Play Console, and bump the Play Console release version to `versionCode = 5` and `versionName = "5.0"`.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> **Summary of Confirmed Choices from Phase 1 Clarification:**
> - **Payment Provider**: Google Play Billing via Google Play Console merchant account.
> - **Feature Gating**: Strictly lock all PRO features (custom domains, verified gold badge, 0% platform fee, print-ready vector QR standee) until a Google Play purchase token is verified.
> - **Developer Payout Mechanism**: Clarified that Google Play handles customer billing directly, deposits net earnings (85% after Google's 15% tier fee) into your Google Play Console Merchant Profile, and automatically transfers payouts to your linked bank account on the 15th of each month.

---

## 1. Overview & Core Concept

### What It Does
- **Closes Free Access Vulnerability**: Eliminates the client-side mock dialog that allowed users to toggle `isProUser = true` without paying.
- **Google Play In-App Billing**: Integrates Google Play Billing (`BillingClient`) to launch official Google Play payment sheets for monthly ($9.99/mo) and annual ($79.00/yr) subscriptions.
- **Strict Gating**: Locks premium features with visual padlock badges and paywall sheets unless an active subscription is verified through Google Play.
- **Developer Payout & Revenue Guide**: An in-app Developer Finance Hub in the Monetize tab detailing step-by-step how Google Play pays out funds into your bank account, how to configure subscriptions in Google Play Console, and how payouts work.
- **Version Bump**: Updates `versionCode` to `5` and `versionName` to `"5.0"` in `app/build.gradle.kts`.

### Target Audience & Persona
- **App Developer / Owner**: Needs legitimate revenue collection where Google handles payments and transfers earnings to their bank.
- **Creators & Influencers**: Upgrade to MultiLink PRO for custom domains, 0% product fees, and verified badges with legitimate billing through Google Play.

---

## 2. User Experience & Visual Design

### Key User Flows

#### Flow 1: Attempting to Access a Locked PRO Feature
1. User taps a PRO-only toggle (e.g., Custom Domain, Verified Gold Badge, or Print-Ready Standee QR).
2. The app detects `isProUser == false` and displays the **MultiLink PRO Paywall Modal** highlighting the plan benefits and pricing.
3. User selects either **Monthly ($9.99/mo)** or **Annual ($79.00/yr • Best Value)**.
4. User taps **"Subscribe via Google Play"**.
5. Google Play Billing sheet opens natively. Only upon successful transaction confirmation and purchase token receipt is `isProUser` activated and stored.

#### Flow 2: Developer Payout Guide & Financial Hub
1. Developer opens the **Monetize** tab.
2. An interactive card **"Developer Payout & Revenue Guide"** is available.
3. Tapping opens a comprehensive walkthrough explaining:
   - **Where Money Lands**: Google Payments Merchant Profile linked to Play Console.
   - **Bank Account Setup**: How to add your IBAN / routing and account number in Play Console.
   - **Payout Schedule**: Paid out automatically on the 15th of each month for the previous month's net earnings.
   - **Google Cut vs Developer Net**: 15% Google service fee under the 15% tier, 85% sent directly to the developer.
   - **Subscription Product IDs**: Explicit product IDs to register in Play Console (`multilink_pro_monthly`, `multilink_pro_annual`).

---

## 3. Key Product Decisions & Trade-Offs

- **Google Play Billing vs Direct Card Processing**:
  - *Chosen Approach*: Official Google Play In-App Billing (`BillingClient`).
  - *Why*: Google Play Developer Policy strictly mandates Google Play In-App Billing for digital subscriptions and in-app feature unlocks. Attempting direct credit card processing inside an Android app for digital unlocks can lead to app suspension.
- **Strict Purchase Verification**:
  - *Chosen Approach*: Query `BillingClient.queryPurchasesAsync()` on launch and on subscription action.
  - *Why*: Prevents tampering or local room spoofing; users cannot access PRO features without a valid subscription record.
- **Graceful Sandbox & Fallback Handling**:
  - *Chosen Approach*: If the app is run outside of Google Play (e.g. debug test build before in-app products are active in Play Console), the UI displays a clear configuration banner explaining how to link in Play Console, rather than silently granting free lifetime access.

---

## 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────┐
│                   MultiLink App                        │
└────────────────────────────────────────────────────────┘
          │                                  │
          ▼                                  ▼
┌───────────────────────────┐    ┌───────────────────────────┐
│     MonetizeScreen        │    │    Locked PRO Features    │
│  • Plan Selector          │    │  • Custom Domain          │
│  • Developer Payout Guide │    │  • Verified Badge         │
│  • Paywall Sheet          │    │  • Vector QR Standee      │
└───────────────────────────┘    └───────────────────────────┘
          │                                  │
          ▼                                  ▼
┌────────────────────────────────────────────────────────────┐
│                  PlayBillingManager                        │
│  • BillingClient lifecycle & connection                    │
│  • queryProductDetails (multilink_pro_monthly/annual)      │
│  • launchBillingFlow (Google Play UI Sheet)                │
│  • PurchasesUpdatedListener & acknowledgePurchase          │
└────────────────────────────────────────────────────────────┘
          │
          ▼
┌────────────────────────────────────────────────────────────┐
│                    Google Play Servers                     │
│  • Charges Customer (Card / Play Balance / Carrier)        │
│  • Records Subscription in Play Console                    │
│  • Net Revenue (85%) -> Google Merchant Account            │
│  • Monthly Wire / Direct Deposit -> Developer Bank         │
└────────────────────────────────────────────────────────────┘
```

### Data Model & Settings
- **`app/build.gradle.kts`**:
  - `versionCode = 5`
  - `versionName = "5.0"`
  - Add `billing-ktx` dependency.
- **`PlayBillingManager.kt`**:
  - Manages `BillingClient` lifecycle, connects asynchronously, queries product details, launches the purchase flow, and verifies subscription tokens.
- **`CreatorProfile.kt` & Room Database**:
  - Stores `isProUser`, `proPlanTier`, and `proPurchaseToken`.
- **`DeveloperPayoutDialog.kt`**:
  - Displays full bank payout setup instructions and revenue flow diagrams.

---

## Verification Plan

### Automated Verification
- Verify build configuration with `compile_applet`.
- Verify unit tests with `gradle :app:testDebugUnitTest`.

### Manual & Flow Verification
1. **Version Code Bump**:
   - Verify `versionCode` is 5 and `versionName` is "5.0" in `app/build.gradle.kts`.
2. **Strict Gating Check**:
   - Open Creator Studio. Verify PRO features (custom domain, verified badge, high-res standee QR) show padlock icons and require subscription.
   - Verify that clicking "Subscribe" no longer immediately toggles PRO for free without Google Play purchase verification.
3. **Developer Payout Guide**:
   - Open Monetize tab -> tap "Developer Payout & Revenue Guide".
   - Confirm all instructions for bank setup, payout schedules, and Play Console subscription IDs are clearly rendered.
