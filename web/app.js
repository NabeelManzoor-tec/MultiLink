/**
 * MultiLink / Linktree Pro Web Engine
 * Real-Time Firestore Sync, Android Bridge & Interactive Creator Features
 */

// 1. Core State & Initial Rich Seed (Matches Android Creator Data)
const DEFAULT_HANDLE = "alexa_aura";

const fallbackProfile = {
  handle: "@alexa_aura",
  cleanHandle: "alexa_aura",
  displayName: "Alexa Rivers",
  headline: "Digital Artist & Creative Technologist",
  bio: "Creating next-gen cyberpunk shaders, 3D toolkits & mentoring ambitious indie creators worldwide 🚀",
  avatarUrl: "images/creator_avatar_default_1793630325308.jpg",
  isVerified: true,
  isProUser: true,
  themeId: "CYBER_NEON",
  tipGoalTitle: "4K Unreal 5 Render Cluster",
  tipGoalCurrent: 1840.0,
  tipGoalTarget: 2500.0
};

const fallbackLinks = [
  {
    id: 1,
    title: "The Digital Frontier: Cyberpunk 3D Toolkit 2026",
    subtitle: "50+ Procedural Shaders, Unreal Engine 5 project & LUTs",
    category: "PRODUCT",
    iconKey: "book",
    price: "$39.00",
    badge: "BESTSELLER",
    isFeatured: true,
    coverImageName: "images/digital_product_mockup_1793630338547.jpg",
    url: "https://gumroad.com/l/cyber-frontier",
    isActive: true
  },
  {
    id: 2,
    title: "1-on-1 Creative Direction & Portfolio Review",
    subtitle: "45-minute live screen-share strategy session & roadmap",
    category: "SERVICE",
    iconKey: "calendar",
    price: "$150 / session",
    badge: "2 SLOTS LEFT",
    isFeatured: true,
    coverImageName: "",
    url: "https://cal.com/alexa-aura/mentorship",
    isActive: true
  },
  {
    id: 3,
    title: "YouTube Channel",
    subtitle: "128k Subscribers • Weekly VFX & Creative Coding Tutorials",
    category: "SOCIAL",
    iconKey: "youtube",
    price: "",
    badge: "",
    isFeatured: false,
    coverImageName: "",
    url: "https://youtube.com/@alexa_aura",
    isActive: true
  },
  {
    id: 4,
    title: "Instagram",
    subtitle: "84.2k Followers • Daily 3D Renders & Aesthetic BTS",
    category: "SOCIAL",
    iconKey: "instagram",
    price: "",
    badge: "",
    isFeatured: false,
    coverImageName: "",
    url: "https://instagram.com/alexa.aura",
    isActive: true
  },
  {
    id: 5,
    title: "TikTok",
    subtitle: "210k Community • Viral Breakdown Reels",
    category: "SOCIAL",
    iconKey: "tiktok",
    price: "",
    badge: "",
    isFeatured: false,
    coverImageName: "",
    url: "https://tiktok.com/@alexa_aura",
    isActive: true
  },
  {
    id: 6,
    title: "X / Twitter",
    subtitle: "Indie Devlogs, Real-Time Graphics Discussions & Drops",
    category: "SOCIAL",
    iconKey: "twitter",
    price: "",
    badge: "",
    isFeatured: false,
    coverImageName: "",
    url: "https://x.com/alexa_aura",
    isActive: true
  },
  {
    id: 7,
    title: "Discord Community Hub",
    subtitle: "Join 4,500+ digital artists, coders & shader engineers",
    category: "SOCIAL",
    iconKey: "discord",
    price: "",
    badge: "COMMUNITY",
    isFeatured: false,
    coverImageName: "",
    url: "https://discord.gg/alexa-creators",
    isActive: true
  },
  {
    id: 8,
    title: "GitHub Open Source Shaders",
    subtitle: "GLSL Compute Shaders & Three.js Demos",
    category: "SOCIAL",
    iconKey: "github",
    price: "",
    badge: "FREE",
    isFeatured: false,
    coverImageName: "",
    url: "https://github.com",
    isActive: true
  }
];

// 2. Determine Active Handle and Theme
const urlParams = new URLSearchParams(window.location.search);
let activeHandle = (urlParams.get("handle") || DEFAULT_HANDLE).replace("@", "").toLowerCase();
let requestedTheme = urlParams.get("theme") || "";

// 3. Vector Icons Dictionary (High fidelity SVGs for Linktree items)
const SVG_ICONS = {
  youtube: `<svg viewBox="0 0 24 24"><path d="M23.498 6.186a3.016 3.016 0 0 0-2.122-2.136C19.505 3.545 12 3.545 12 3.545s-7.505 0-9.377.505A3.017 3.017 0 0 0 .502 6.186C0 8.07 0 12 0 12s0 3.93.502 5.814a3.016 3.016 0 0 0 2.122 2.136c1.871.505 9.376.505 9.376.505s7.505 0 9.377-.505a3.015 3.015 0 0 0 2.122-2.136C24 15.93 24 12 24 12s0-3.93-.502-5.814zM9.545 15.568V8.432L15.818 12l-6.273 3.568z"/></svg>`,
  instagram: `<svg viewBox="0 0 24 24"><path d="M12 2.163c3.204 0 3.584.012 4.85.07 3.252.148 4.771 1.691 4.919 4.919.058 1.265.069 1.645.069 4.849 0 3.205-.012 3.584-.069 4.849-.149 3.225-1.664 4.771-4.919 4.919-1.266.058-1.644.07-4.85.07-3.204 0-3.584-.012-4.849-.07-3.26-.149-4.771-1.699-4.919-4.92-.058-1.265-.07-1.644-.07-4.849 0-3.204.013-3.583.07-4.849.149-3.227 1.664-4.771 4.919-4.919 1.266-.057 1.645-.069 4.849-.069zm0-2.163c-3.259 0-3.667.014-4.947.072-4.358.2-6.78 2.618-6.98 6.98-.059 1.281-.073 1.689-.073 4.948 0 3.259.014 3.668.072 4.948.2 4.358 2.618 6.78 6.98 6.98 1.281.058 1.689.072 4.948.072 3.259 0 3.668-.014 4.948-.072 4.354-.2 6.782-2.618 6.979-6.98.059-1.28.073-1.689.073-4.948 0-3.259-.014-3.667-.072-4.947-.196-4.354-2.617-6.78-6.979-6.98-1.281-.059-1.69-.073-4.949-.073zm0 5.838c-3.403 0-6.162 2.759-6.162 6.162s2.759 6.163 6.162 6.163 6.162-2.759 6.162-6.163c0-3.403-2.759-6.162-6.162-6.162zm0 10.162c-2.209 0-4-1.79-4-4 0-2.209 1.791-4 4-4s4 1.791 4 4c0 2.21-1.791 4-4 4zm6.406-11.845c-.796 0-1.441.645-1.441 1.44s.645 1.44 1.441 1.44c.795 0 1.439-.645 1.439-1.44s-.644-1.44-1.439-1.44z"/></svg>`,
  tiktok: `<svg viewBox="0 0 24 24"><path d="M12.525.02c1.31 0 2.6.32 3.73.93v4.44a5.6 5.6 0 0 1-3.6-1.32v8.95a7.03 7.03 0 1 1-7.03-7.03c.53 0 1.05.06 1.54.17V10.7a2.53 2.53 0 1 0 1.36 2.24V.02h4z"/></svg>`,
  twitter: `<svg viewBox="0 0 24 24"><path d="M18.244 2.25h3.308l-7.227 8.26 8.502 11.24H16.17l-5.214-6.817L4.99 21.75H1.68l7.73-8.835L1.254 2.25H8.08l4.713 6.231zm-1.161 17.52h1.833L7.084 4.126H5.117z"/></svg>`,
  discord: `<svg viewBox="0 0 24 24"><path d="M20.317 4.37a19.791 19.791 0 0 0-4.885-1.515.074.074 0 0 0-.079.037c-.21.375-.444.864-.608 1.25a18.27 18.27 0 0 0-5.487 0 12.64 12.64 0 0 0-.617-1.25.077.077 0 0 0-.079-.037A19.736 19.736 0 0 0 3.677 4.37a.07.07 0 0 0-.032.027C.533 9.046-.32 13.58.099 18.057a.082.082 0 0 0 .031.057 19.9 19.9 0 0 0 5.993 3.03.078.078 0 0 0 .084-.028c.462-.63.874-1.295 1.226-1.994.021-.041.001-.09-.041-.106a13.107 13.107 0 0 1-1.872-.892.077.077 0 0 1-.008-.128 10.2 10.2 0 0 0 .372-.292.074.074 0 0 1 .077-.01c3.929 1.793 8.18 1.793 12.061 0a.074.074 0 0 1 .078.01c.12.098.246.198.373.292a.077.077 0 0 1-.006.127 12.299 12.299 0 0 1-1.873.894.077.077 0 0 0-.041.107c.36.698.772 1.362 1.225 1.993a.076.076 0 0 0 .084.028 19.839 19.839 0 0 0 6.002-3.03.077.077 0 0 0 .032-.054c.5-5.177-.838-9.674-3.549-13.66a.061.061 0 0 0-.031-.028zM8.02 15.33c-1.183 0-2.157-1.085-2.157-2.419 0-1.333.956-2.419 2.157-2.419 1.21 0 2.176 1.096 2.157 2.42 0 1.333-.956 2.418-2.157 2.418zm7.975 0c-1.183 0-2.157-1.085-2.157-2.419 0-1.333.955-2.419 2.157-2.419 1.21 0 2.176 1.096 2.157 2.42 0 1.333-.946 2.418-2.157 2.418z"/></svg>`,
  github: `<svg viewBox="0 0 24 24"><path d="M12 0C5.37 0 0 5.37 0 12c0 5.31 3.435 9.795 8.205 11.385.6.105.825-.255.825-.57 0-.285-.015-1.23-.015-2.235-3.015.555-3.795-.735-4.035-1.41-.135-.345-.72-1.41-1.23-1.695-.42-.225-1.02-.78-.015-.795.945-.015 1.62.87 1.845 1.23 1.08 1.815 2.805 1.305 3.495.99.105-.78.42-1.305.765-1.605-2.67-.3-5.46-1.335-5.46-5.925 0-1.305.465-2.385 1.23-3.225-.12-.3-.54-1.53.12-3.18 0 0 1.005-.315 3.3 1.23.96-.27 1.98-.405 3-.405s2.04.135 3 .405c2.295-1.56 3.3-1.23 3.3-1.23.66 1.65.24 2.88.12 3.18.765.84 1.23 1.905 1.23 3.225 0 4.605-2.805 5.625-5.475 5.925.435.375.81 1.095.81 2.22 0 1.605-.015 2.895-.015 3.3 0 .315.225.69.825.57A12.02 12.02 0 0 0 24 12c0-6.63-5.37-12-12-12z"/></svg>`,
  spotify: `<svg viewBox="0 0 24 24"><path d="M12 0C5.4 0 0 5.4 0 12s5.4 12 12 12 12-5.4 12-12S18.66 0 12 0zm5.521 17.34c-.24.359-.66.48-1.021.24-2.82-1.74-6.36-2.101-10.561-1.141-.418.122-.779-.179-.899-.539-.12-.421.18-.78.54-.9 4.56-1.021 8.52-.6 11.64 1.32.42.18.479.659.301 1.02zm1.44-3.3c-.301.42-.841.6-1.262.3-3.239-1.98-8.159-2.58-11.939-1.38-.479.12-1.02-.12-1.14-.6-.12-.48.12-1.021.6-1.141C9.6 9.9 15 10.561 18.72 12.84c.361.181.54.78.241 1.2zm.12-3.36C15.24 8.4 8.82 8.16 5.16 9.301c-.6.179-1.2-.181-1.38-.721-.18-.601.18-1.2.72-1.381 4.26-1.26 11.28-1.02 15.721 1.621.539.3.719 1.02.419 1.56-.299.421-1.02.599-1.559.3z"/></svg>`,
  email: `<svg viewBox="0 0 24 24"><path d="M20 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 4l-8 5-8-5V6l8 5 8-5v2z"/></svg>`,
  default_link: `<svg viewBox="0 0 24 24"><path d="M3.9 12c0-1.71 1.39-3.1 3.1-3.1h4V7H7c-2.76 0-5 2.24-5 5s2.24 5 5 5h4v-1.9H7c-1.71 0-3.1-1.39-3.1-3.1zM8 13h8v-2H8v2zm9-6h-4v1.9h4c1.71 0 3.1 1.39 3.1 3.1s-1.39 3.1-3.1 3.1h-4V17h4c2.76 0 5-2.24 5-5s-2.24-5-5-5z"/></svg>`
};

// 4. UI Elements References
const creatorAvatar = document.getElementById("creatorAvatar");
const creatorName = document.getElementById("creatorName");
const creatorHandle = document.getElementById("creatorHandle");
const creatorBio = document.getElementById("creatorBio");
const verifiedBadge = document.getElementById("verifiedBadge");
const socialRibbon = document.getElementById("socialRibbon");

const tipSection = document.getElementById("tipSection");
const tipGoalTitle = document.getElementById("tipGoalTitle");
const tipProgressBar = document.getElementById("tipProgressBar");
const tipGoalAmount = document.getElementById("tipGoalAmount");
const tipGoalTarget = document.getElementById("tipGoalTarget");
const openTipBtn = document.getElementById("openTipBtn");

const productsSection = document.getElementById("productsSection");
const productsList = document.getElementById("productsList");
const linksList = document.getElementById("linksList");
const syncStatus = document.getElementById("syncStatus");

// Modals
const tipModal = document.getElementById("tipModal");
const closeTipModal = document.getElementById("closeTipModal");
const confirmTipBtn = document.getElementById("confirmTipBtn");
const customTipInput = document.getElementById("customTipInput");
const tipDonorMessage = document.getElementById("tipDonorMessage");
let selectedTipAmount = 5;

const productModal = document.getElementById("productModal");
const closeProductModal = document.getElementById("closeProductModal");
const modalProductTitle = document.getElementById("modalProductTitle");
const modalProductDesc = document.getElementById("modalProductDesc");
const modalProductPrice = document.getElementById("modalProductPrice");
const modalProductImg = document.getElementById("modalProductImg");
const confirmPurchaseBtn = document.getElementById("confirmPurchaseBtn");
let activeProductToBuy = null;

const shareModal = document.getElementById("shareModal");
const topShareBtn = document.getElementById("topShareBtn");
const closeShareModal = document.getElementById("closeShareModal");
const shareUrlInput = document.getElementById("shareUrlInput");
const copyShareUrlBtn = document.getElementById("copyShareUrlBtn");
const copyBtnLabel = document.getElementById("copyBtnLabel");
const nativeShareBtn = document.getElementById("nativeShareBtn");
const qrImage = document.getElementById("qrImage");

const mediaModal = document.getElementById("mediaModal");
const closeMediaModal = document.getElementById("closeMediaModal");
const mediaContainer = document.getElementById("mediaContainer");
const mediaModalTitle = document.getElementById("mediaModalTitle");

// Newsletter
const newsletterForm = document.getElementById("newsletterForm");
const newsletterEmail = document.getElementById("newsletterEmail");
const newsletterSuccess = document.getElementById("newsletterSuccess");

// Current active in-memory state
let currentProfile = { ...fallbackProfile };
let currentLinks = [...fallbackLinks];

// 5. Device Detection
function getDeviceType() {
  const ua = navigator.userAgent;
  if (/android/i.test(ua)) return "Mobile (Android)";
  if (/iPhone|iPad|iPod/i.test(ua)) return "Mobile (iOS)";
  if (/Mac/i.test(ua)) return "Desktop (Mac)";
  if (/Win/i.test(ua)) return "Desktop (Windows)";
  return "Web Browser";
}

// 6. Theme Application
function applyTheme(themeId) {
  const validThemes = ["CYBER_NEON", "LUXURY_DARK", "SUNSET_BLAZE", "EMERALD_MINT", "MINIMAL_CLEAN"];
  const target = validThemes.includes(themeId) ? themeId : "CYBER_NEON";
  document.body.setAttribute("data-theme", target);
}

// 7. Profile Rendering
function renderProfile(profile) {
  currentProfile = { ...currentProfile, ...profile };
  
  creatorName.textContent = profile.displayName || "Creator";
  creatorHandle.textContent = profile.handle || "@" + activeHandle;
  creatorBio.textContent = profile.bio || "";
  
  if (profile.avatarUrl) {
    creatorAvatar.src = profile.avatarUrl;
    document.getElementById("shareAvatarSnippet").src = profile.avatarUrl;
  }
  
  document.getElementById("shareNameSnippet").textContent = profile.displayName || "Creator";
  document.getElementById("shareHandleSnippet").textContent = profile.handle || "@" + activeHandle;
  
  verifiedBadge.style.display = (profile.isVerified || profile.isProUser) ? "flex" : "none";

  // Apply Theme
  const theme = requestedTheme || profile.themeId || "CYBER_NEON";
  applyTheme(theme);

  // Tip Jar
  tipGoalTitle.textContent = profile.tipGoalTitle || "Support Creative Projects";
  const current = profile.tipGoalCurrent || 0;
  const target = profile.tipGoalTarget || 2500;
  tipGoalAmount.textContent = `$${current.toLocaleString("en-US", { minimumFractionDigits: 2 })} raised`;
  tipGoalTarget.textContent = `Goal: $${target.toLocaleString("en-US", { minimumFractionDigits: 2 })}`;

  const percent = Math.min(100, Math.max(0, (current / target) * 100));
  tipProgressBar.style.width = `${percent.toFixed(1)}%`;
}

// 8. Social Ribbon Rendering (Linktree Style Icon Bar)
function renderSocialRibbon(links) {
  socialRibbon.innerHTML = "";
  
  const socialCandidates = links.filter(l => {
    const key = (l.iconKey || "").toLowerCase();
    const title = (l.title || "").toLowerCase();
    return ["youtube", "instagram", "tiktok", "twitter", "x", "discord", "github", "spotify", "email"].includes(key) ||
           title.includes("youtube") || title.includes("instagram") || title.includes("tiktok") || 
           title.includes("twitter") || title.includes("discord") || title.includes("github") || title.includes("spotify");
  });

  const uniquePlatforms = new Set();
  socialCandidates.forEach(link => {
    let platform = (link.iconKey || "").toLowerCase();
    if (!SVG_ICONS[platform]) {
      const t = (link.title || "").toLowerCase();
      if (t.includes("youtube")) platform = "youtube";
      else if (t.includes("instagram")) platform = "instagram";
      else if (t.includes("tiktok")) platform = "tiktok";
      else if (t.includes("twitter") || t.includes(" x")) platform = "twitter";
      else if (t.includes("discord")) platform = "discord";
      else if (t.includes("github")) platform = "github";
      else if (t.includes("spotify")) platform = "spotify";
      else platform = "default_link";
    }

    if (uniquePlatforms.has(platform)) return;
    uniquePlatforms.add(platform);

    const btn = document.createElement("a");
    btn.className = "social-btn";
    btn.href = link.url || "#";
    btn.target = "_blank";
    btn.rel = "noopener noreferrer";
    btn.setAttribute("aria-label", link.title || platform);
    btn.innerHTML = SVG_ICONS[platform] || SVG_ICONS.default_link;

    btn.addEventListener("click", () => {
      recordTelemetry(link, 0.0);
    });

    socialRibbon.appendChild(btn);
  });
}

// 9. Links & Digital Products Rendering
function renderLinks(links) {
  currentLinks = links;
  productsList.innerHTML = "";
  linksList.innerHTML = "";

  const activeLinks = links.filter(l => l.isActive !== false);
  const products = activeLinks.filter(l => l.category === "PRODUCT");
  const standardLinks = activeLinks.filter(l => l.category !== "PRODUCT");

  // Update Social Ribbon
  renderSocialRibbon(activeLinks);

  // 1. Digital Products Section
  if (products.length > 0) {
    productsSection.style.display = "flex";
    products.forEach(product => {
      const card = document.createElement("div");
      card.className = "product-card";
      
      const thumbSrc = product.coverImageName ? product.coverImageName : "images/digital_product_mockup_1793630338547.jpg";

      card.innerHTML = `
        <img src="${thumbSrc}" alt="${product.title}" class="product-thumb">
        <div class="link-info" style="flex: 1;">
          <div class="link-title">${product.title}</div>
          <div class="link-desc">${product.subtitle || "Instant digital asset package"}</div>
        </div>
        <button class="product-price-pill">${product.price || "$39.00"}</button>
      `;

      card.addEventListener("click", () => {
        openProductModal(product);
      });

      productsList.appendChild(card);
    });
  } else {
    productsSection.style.display = "none";
  }

  // 2. Standard Links (Linktree Buttons)
  standardLinks.forEach(link => {
    const card = document.createElement("div");
    card.className = `link-card ${link.isFeatured ? "featured" : ""}`;

    const iconKey = (link.iconKey || "").toLowerCase();
    let iconContent = "🔗";
    if (iconKey === "youtube" || link.url.includes("youtube.com")) iconContent = "▶";
    else if (iconKey === "instagram") iconContent = "📷";
    else if (iconKey === "tiktok") iconContent = "🎵";
    else if (iconKey === "twitter") iconContent = "𝕏";
    else if (iconKey === "discord") iconContent = "💬";
    else if (iconKey === "github") iconContent = "⌥";
    else if (iconKey === "spotify") iconContent = "🎧";
    else if (iconKey === "coffee") iconContent = "☕";
    else if (iconKey === "calendar") iconContent = "📅";
    else if (iconKey === "book") iconContent = "📖";

    const badgeHtml = link.badge ? `<span class="link-badge">${link.badge}</span>` : "";
    const featuredPill = link.isFeatured ? `<div class="featured-pill">⭐ Featured</div>` : "";

    card.innerHTML = `
      ${featuredPill}
      <div class="link-left">
        <div class="link-icon-box">${iconContent}</div>
        <div class="link-info">
          <div class="link-title">${link.title}</div>
          ${link.subtitle ? `<div class="link-desc">${link.subtitle}</div>` : ""}
        </div>
      </div>
      <div class="link-right">
        ${badgeHtml}
        <span class="link-arrow">→</span>
      </div>
    `;

    card.addEventListener("click", () => {
      recordTelemetry(link, 0.0);
      handleLinkClickAction(link);
    });

    linksList.appendChild(card);
  });
}

// 10. Handle Link Clicks (Smart Preview vs External Link)
function handleLinkClickAction(link) {
  const url = link.url || "";
  
  // YouTube inline preview
  if (url.includes("youtube.com/watch") || url.includes("youtu.be/")) {
    let videoId = "";
    if (url.includes("v=")) {
      videoId = url.split("v=")[1]?.split("&")[0];
    } else if (url.includes("youtu.be/")) {
      videoId = url.split("youtu.be/")[1]?.split("?")[0];
    }
    
    if (videoId) {
      openMediaModal(link.title, `https://www.youtube.com/embed/${videoId}?autoplay=1`);
      return;
    }
  }

  // Tip Link
  if (link.category === "TIP") {
    openTipModal();
    return;
  }

  // Standard external URL
  if (url.startsWith("http")) {
    window.open(url, "_blank");
  }
}

// 11. Telemetry Logger (Works with Android Bridge & Firestore)
async function recordTelemetry(link, revenue = 0.0) {
  const event = {
    linkId: link.id || 0,
    linkTitle: link.title || "Link",
    category: link.category || "STANDARD",
    timestamp: Date.now(),
    sourcePlatform: "Public Web Version",
    country: "Global Web",
    device: getDeviceType(),
    revenueAmount: revenue
  };

  // If inside Android WebView, pass to native bridge
  if (window.AndroidMultiLink && typeof window.AndroidMultiLink.recordClick === "function") {
    try {
      window.AndroidMultiLink.recordClick(event.linkId, event.linkTitle, event.category, event.revenueAmount);
    } catch (e) {
      console.warn("AndroidMultiLink bridge call note:", e);
    }
  }

  // Also log to Firestore via REST if online
  try {
    const firestoreUrl = `https://firestore.googleapis.com/v1/projects/gen-lang-client-0468598937/databases/ai-studio-android-multilin-ab396dc5-108b-49e4-8b48-2477cc07b114/documents/public_profiles/${activeHandle}/events`;
    fetch(firestoreUrl, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        fields: {
          linkId: { integerValue: event.linkId },
          linkTitle: { stringValue: event.linkTitle },
          category: { stringValue: event.category },
          timestamp: { integerValue: event.timestamp },
          sourcePlatform: { stringValue: event.sourcePlatform },
          country: { stringValue: event.country },
          device: { stringValue: event.device },
          revenueAmount: { doubleValue: event.revenueAmount }
        }
      })
    }).catch(() => {});
  } catch (e) {}
}

// 12. Modal & Interaction Handlers
function openTipModal() {
  tipModal.classList.add("active");
}

function closeAllModals() {
  tipModal.classList.remove("active");
  productModal.classList.remove("active");
  shareModal.classList.remove("active");
  mediaModal.classList.remove("active");
  mediaContainer.innerHTML = "";
}

openTipBtn.addEventListener("click", openTipModal);
closeTipModal.addEventListener("click", closeAllModals);

document.querySelectorAll(".amount-btn").forEach(btn => {
  btn.addEventListener("click", () => {
    document.querySelectorAll(".amount-btn").forEach(b => b.classList.remove("selected"));
    btn.classList.add("selected");
    selectedTipAmount = parseFloat(btn.dataset.amount);
    confirmTipBtn.textContent = `Send $${selectedTipAmount} Tip ☕`;
    customTipInput.value = "";
  });
});

customTipInput.addEventListener("input", (e) => {
  const val = parseFloat(e.target.value);
  if (!isNaN(val) && val > 0) {
    document.querySelectorAll(".amount-btn").forEach(b => b.classList.remove("selected"));
    selectedTipAmount = val;
    confirmTipBtn.textContent = `Send $${val.toFixed(2)} Tip ☕`;
  }
});

confirmTipBtn.addEventListener("click", async () => {
  confirmTipBtn.disabled = true;
  confirmTipBtn.textContent = "Processing Tip...";

  const donorNote = tipDonorMessage.value.trim();
  const revenue = selectedTipAmount;

  await recordTelemetry({
    id: 0,
    title: donorNote ? `Tip $${revenue} - "${donorNote}"` : `Tip $${revenue}`,
    category: "TIP"
  }, revenue);

  // Update in-memory tip progress immediately
  currentProfile.tipGoalCurrent = (currentProfile.tipGoalCurrent || 0) + revenue;
  renderProfile(currentProfile);

  // Trigger celebration confetti
  triggerConfetti();

  setTimeout(() => {
    alert(`Thank you so much! ☕ Your $${revenue.toFixed(2)} contribution has been received.`);
    closeAllModals();
    confirmTipBtn.disabled = false;
    confirmTipBtn.textContent = `Send $${selectedTipAmount} Tip ☕`;
    tipDonorMessage.value = "";
  }, 400);
});

// Digital Product Checkout
function openProductModal(product) {
  activeProductToBuy = product;
  modalProductTitle.textContent = product.title;
  modalProductDesc.textContent = product.subtitle || "Instant digital asset download with perpetual commercial license.";
  modalProductPrice.textContent = product.price || "$39.00";
  if (product.coverImageName) {
    modalProductImg.src = product.coverImageName;
  }
  productModal.classList.add("active");
}

closeProductModal.addEventListener("click", closeAllModals);

confirmPurchaseBtn.addEventListener("click", async () => {
  if (!activeProductToBuy) return;
  confirmPurchaseBtn.disabled = true;
  confirmPurchaseBtn.textContent = "Creating License...";

  const priceClean = parseFloat((activeProductToBuy.price || "").replace(/[^0-9.]/g, "")) || 39.0;
  await recordTelemetry(activeProductToBuy, priceClean);

  triggerConfetti();

  // Instant simulated digital delivery download
  const licenseFile = `MultiLink Digital License & Delivery\n==================================\nItem: ${activeProductToBuy.title}\nLicense Holder: Verified Customer\nLicense Type: Commercial & Extended Studio Use\nTransaction ID: TX-PLAY-${Date.now()}\n\nDownload Link: https://assets.multilink.io/download/pack_${Date.now()}.zip\n\nThank you for supporting independent creator ${currentProfile.displayName}!`;
  const blob = new Blob([licenseFile], { type: "text/plain" });
  const a = document.createElement("a");
  a.href = URL.createObjectURL(blob);
  a.download = `${activeProductToBuy.title.replace(/[^a-zA-Z0-9]/g, "_")}_License.txt`;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);

  setTimeout(() => {
    alert(`Purchase Complete! Your instant download and commercial license have started.`);
    closeAllModals();
    confirmPurchaseBtn.disabled = false;
    confirmPurchaseBtn.textContent = "Instant Download & License";
  }, 400);
});

// Share Modal & QR Code
function openShareModal() {
  const currentUrl = window.location.href;
  shareUrlInput.value = currentUrl;
  qrImage.src = `https://api.qrserver.com/v1/create-qr-code/?size=240x240&data=${encodeURIComponent(currentUrl)}`;
  shareModal.classList.add("active");
}

topShareBtn.addEventListener("click", openShareModal);
document.getElementById("footerShareBtn").addEventListener("click", (e) => {
  e.preventDefault();
  openShareModal();
});
closeShareModal.addEventListener("click", closeAllModals);

copyShareUrlBtn.addEventListener("click", () => {
  navigator.clipboard.writeText(shareUrlInput.value);
  copyBtnLabel.textContent = "Copied!";
  setTimeout(() => {
    copyBtnLabel.textContent = "Copy Link";
  }, 2000);
});

nativeShareBtn.addEventListener("click", async () => {
  if (navigator.share) {
    try {
      await navigator.share({
        title: `${currentProfile.displayName} on MultiLink`,
        text: `Check out ${currentProfile.displayName}'s links & digital creations:`,
        url: shareUrlInput.value
      });
    } catch (e) {}
  } else {
    copyShareUrlBtn.click();
  }
});

// Media Modal
function openMediaModal(title, embedUrl) {
  mediaModalTitle.textContent = title;
  mediaContainer.innerHTML = `<iframe src="${embedUrl}" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>`;
  mediaModal.classList.add("active");
}

closeMediaModal.addEventListener("click", closeAllModals);

// Newsletter Form
newsletterForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  const email = newsletterEmail.value.trim();
  if (!email) return;

  await recordTelemetry({
    id: 0,
    title: `Subscriber: ${email}`,
    category: "LEAD"
  }, 0.0);

  newsletterForm.style.display = "none";
  newsletterSuccess.style.display = "block";
});

// 13. Confetti Animation Engine
function triggerConfetti() {
  const canvas = document.getElementById("confettiCanvas");
  if (!canvas) return;
  const ctx = canvas.getContext("2d");
  canvas.width = window.innerWidth;
  canvas.height = window.innerHeight;

  const particles = [];
  const colors = ["#06b6d4", "#6366f1", "#10b981", "#f59e0b", "#f43f5e", "#fbbf24"];

  for (let i = 0; i < 75; i++) {
    particles.push({
      x: canvas.width / 2,
      y: canvas.height / 2,
      vx: (Math.random() - 0.5) * 16,
      vy: (Math.random() - 0.7) * 18,
      size: Math.random() * 8 + 4,
      color: colors[Math.floor(Math.random() * colors.length)],
      alpha: 1.0,
      decay: Math.random() * 0.02 + 0.015
    });
  }

  function frame() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    let active = false;

    particles.forEach(p => {
      p.x += p.vx;
      p.y += p.vy;
      p.vy += 0.4; // gravity
      p.alpha -= p.decay;

      if (p.alpha > 0) {
        active = true;
        ctx.fillStyle = p.color;
        ctx.globalAlpha = p.alpha;
        ctx.fillRect(p.x, p.y, p.size, p.size);
      }
    });

    if (active) {
      requestAnimationFrame(frame);
    } else {
      ctx.clearRect(0, 0, canvas.width, canvas.height);
    }
  }

  frame();
}

// 14. Real-Time Data Sync Coordinator
async function fetchFirestoreData() {
  try {
    const profileUrl = `https://firestore.googleapis.com/v1/projects/gen-lang-client-0468598937/databases/ai-studio-android-multilin-ab396dc5-108b-49e4-8b48-2477cc07b114/documents/public_profiles/${activeHandle}`;
    const res = await fetch(profileUrl);
    if (res.ok) {
      const data = await res.json();
      if (data.fields) {
        const fields = data.fields;
        renderProfile({
          displayName: fields.displayName?.stringValue,
          handle: fields.handle?.stringValue,
          bio: fields.bio?.stringValue,
          avatarUrl: fields.avatarDrawableName?.stringValue ? `images/${fields.avatarDrawableName.stringValue}.jpg` : fallbackProfile.avatarUrl,
          themeId: fields.themeId?.stringValue,
          isVerified: fields.isVerified?.booleanValue,
          isProUser: fields.isProUser?.booleanValue,
          tipGoalTitle: fields.tipGoalTitle?.stringValue,
          tipGoalCurrent: fields.tipGoalCurrent?.doubleValue || fields.tipGoalCurrent?.integerValue,
          tipGoalTarget: fields.tipGoalTarget?.doubleValue || fields.tipGoalTarget?.integerValue
        });
        syncStatus.textContent = "Live Synced";
      }
    }

    const linksUrl = `https://firestore.googleapis.com/v1/projects/gen-lang-client-0468598937/databases/ai-studio-android-multilin-ab396dc5-108b-49e4-8b48-2477cc07b114/documents/public_profiles/${activeHandle}/links`;
    const linksRes = await fetch(linksUrl);
    if (linksRes.ok) {
      const linksData = await linksRes.json();
      if (linksData.documents && linksData.documents.length > 0) {
        const parsedLinks = linksData.documents.map(doc => {
          const f = doc.fields;
          return {
            id: f.id?.integerValue || 0,
            title: f.title?.stringValue || "Link",
            subtitle: f.subtitle?.stringValue || "",
            url: f.url?.stringValue || "#",
            category: f.category?.stringValue || "STANDARD",
            iconKey: f.iconKey?.stringValue || "",
            price: f.price?.stringValue || "",
            badge: f.badge?.stringValue || "",
            isFeatured: f.isFeatured?.booleanValue || false,
            isActive: f.isActive?.booleanValue !== false,
            coverImageName: f.coverImageName?.stringValue ? `images/${f.coverImageName.stringValue}.jpg` : ""
          };
        });
        renderLinks(parsedLinks);
      }
    }
  } catch (err) {
    console.log("Firestore background sync note:", err);
  }
}

// 15. Check for Android WebView Bridge
function checkAndroidBridge() {
  if (window.AndroidMultiLink) {
    try {
      if (typeof window.AndroidMultiLink.getProfileJson === "function") {
        const rawProfile = window.AndroidMultiLink.getProfileJson();
        if (rawProfile) {
          const prof = JSON.parse(rawProfile);
          renderProfile(prof);
        }
      }

      if (typeof window.AndroidMultiLink.getLinksJson === "function") {
        const rawLinks = window.AndroidMultiLink.getLinksJson();
        if (rawLinks) {
          const lks = JSON.parse(rawLinks);
          if (Array.isArray(lks) && lks.length > 0) {
            renderLinks(lks);
          }
        }
      }
    } catch (e) {
      console.warn("Error parsing Android bridge data:", e);
    }
  }
}

// 16. Initialize Application
(function init() {
  // 1. Render immediate rich seed so page is instantly complete & never blank
  renderProfile(fallbackProfile);
  renderLinks(fallbackLinks);

  // 2. Check Android Bridge (if running inside app's WebView)
  checkAndroidBridge();

  // 3. Connect to live Firestore for cloud updates
  fetchFirestoreData();

  // Poll cloud every 15s when active on web
  setInterval(fetchFirestoreData, 15000);
})();
