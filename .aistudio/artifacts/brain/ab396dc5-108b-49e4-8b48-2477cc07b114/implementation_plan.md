# Google Play Console: Underscore Product IDs & Backend Billing Plan

Exact Google Play Console form values using standard underscore formatting (`1_pro_monthly`, `2_pro_annual`, `1_tip_5`), with updated pricing ($9.99/mo and $69.99/yr), and dual-alias backend billing support.

---

### User Review & Critical Decisions

> [!IMPORTANT]
> - **Product ID Format**: Standard underscores (`_`) with number prefix (`1_pro_monthly`, `2_pro_annual`, `1_tip_5`).
> - **Updated Pricing**:
>   - Monthly Subscription: **$9.99 / month**
>   - Annual Subscription: **$69.99 / year**
> - **Zero UI Additions**: Handled strictly in the backend, billing client, and data layer without adding extra buttons to the UI.
> - **Universal Backend Compatibility**: `PlayBillingManager.kt` will query both underscore IDs (`1_pro_monthly`) and hyphenated aliases (`1-pro-monthly`) so Google Play resolves whichever format is registered in your console.

- **Confirmed Decision 1**: Use underscore Product IDs (`1_pro_monthly`, `2_pro_annual`, `1_tip_5`, `2_tip_10`, `3_tip_25`, `4_product_lut`).
- **Confirmed Decision 2**: Monthly package set to $9.99/month and Annual package set to $69.99/year.
- **Confirmed Decision 3**: Automatic token acknowledgment, consumable tip support, and real-time Firestore persistence.

---

## 1. Google Play Console Form Values (Copy & Paste)

### Part A: Subscriptions (Recurring)
*Console location: **Monetize with Play** > **Products** > **Subscriptions***

#### 1. PRO Monthly Subscription ($9.99 / month)
- **Product ID**:
  ```
  1_pro_monthly
  ```
- **Name**:
  ```
  MultiLink PRO Monthly
  ```
- **Base Plan ID**: `pro-monthly-base`
- **Type**: Auto-renewing (1 month)
- **Price**: **$9.99**

#### 2. PRO Annual Subscription ($69.99 / year)
- **Product ID**:
  ```
  2_pro_annual
  ```
- **Name**:
  ```
  MultiLink PRO Annual
  ```
- **Base Plan ID**: `pro-annual-base`
- **Type**: Auto-renewing (1 year)
- **Price**: **$69.99**

---

### Part B: In-App Products (One-Time Purchases)
*Console location: **Monetize with Play** > **Products** > **In-app products***

#### 1. Fan Support Tip ($5.00)
- **Product ID**:
  ```
  1_tip_5
  ```
- **Name**: `Fan Support Tip $5`
- **Description**: `Direct fan contribution to support creative work and content.`
- **Price**: **$5.00**
- **Status**: Active

#### 2. Fan Support Tip ($10.00)
- **Product ID**:
  ```
  2_tip_10
  ```
- **Name**: `Fan Support Tip $10`
- **Description**: `Direct fan contribution to support creative work and content.`
- **Price**: **$10.00**
- **Status**: Active

#### 3. Fan Support Tip ($25.00)
- **Product ID**:
  ```
  3_tip_25
  ```
- **Name**: `Fan Support Tip $25`
- **Description**: `VIP fan contribution to sponsor creator projects.`
- **Price**: **$25.00**
- **Status**: Active

#### 4. Digital Product: Cinematic 3D LUT Pack ($39.00)
- **Product ID**:
  ```
  4_product_lut
  ```
- **Name**: `Cinematic 3D LUT Pack`
- **Description**: `Professional 3D LUT color grading presets with lifetime commercial license.`
- **Price**: **$39.00**
- **Status**: Active

---

## 2. Backend & Data Layer Integration

```
┌────────────────────────────────────────────────────────┐
│              Google Play Store Billing API             │
│  SUBS:  1_pro_monthly ($9.99), 2_pro_annual ($69.99)   │
│  INAPP: 1_tip_5, 2_tip_10, 3_tip_25, 4_product_lut     │
└──────────────────────────┬─────────────────────────────┘
                           │
                           ▼
┌────────────────────────────────────────────────────────┐
│           PlayBillingManager.kt (Backend Engine)       │
│  - Queries underscore IDs (and hyphenated aliases)     │
│  - Auto-acknowledges PRO subscriptions & LUT purchases │
│  - Auto-consumes fan tips for repeat tipping           │
└──────────────────────────┬─────────────────────────────┘
                           │
                           ▼
┌────────────────────────────────────────────────────────┐
│             Firebase Firestore Persistence             │
│  - users/{userId}/transactions/{orderId}               │
│  - public_profiles/{handle}/transactions/{orderId}     │
│  - Activates PRO status and increments tipGoalCurrent  │
└────────────────────────────────────────────────────────┘
```

---

## 3. Implementation Steps

1. **`PlayBillingManager.kt`**:
   - Register underscore IDs (`1_pro_monthly`, `2_pro_annual`, `1_tip_5`, `2_tip_10`, `3_tip_25`, `4_product_lut`) and aliases.
   - Query both `BillingClient.ProductType.SUBS` and `BillingClient.ProductType.INAPP`.
   - Add automated acknowledgment and consumption logic.
2. **`MultiLinkViewModel.kt`**:
   - Update subscription amounts to $9.99 (monthly) and $69.99 (annual).
   - Sync completed orders directly to Firestore via `FirestoreSyncManager.recordTransaction`.
3. **`MonetizeScreen.kt`**:
   - Update subscription display text to show $9.99/mo and $69.99/yr with zero new UI buttons added.
4. **Verification**:
   - Build with `compile_applet`.
   - Run tests with `gradle :app:testDebugUnitTest`.
