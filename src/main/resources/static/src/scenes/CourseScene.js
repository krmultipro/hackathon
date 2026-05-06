const COURSE_THEME = {
  bgTop: 0x07111f,
  bgBottom: 0x1a1031,
  cyan: 0x49d6ff,
  yellow: 0xffd84a,
  teal: 0x34f5c5,
  panel: 0x0d182b,
  panelSoft: 0x102138,
  text: 0xf7fbff,
  muted: 0xc6d7ea,
  line: 0x35557c,
};

export default class CourseScene extends Phaser.Scene {
  constructor() {
    super({ key: "CourseScene" });
  }

  create() {
    const { width, height } = this.scale;

    this.bgGradient = this.add.graphics();
    this.bgGlow = this.add.graphics();
    this.grid = this.add.graphics();

    this.heroShadow = this.add.rectangle(0, 0, 10, 10, 0x000000, 0.18);
    this.heroPanel = this.add
      .rectangle(0, 0, 10, 10, COURSE_THEME.panel, 0.88)
      .setStrokeStyle(2, COURSE_THEME.line, 0.6);

    this.titleText = this.add
      .text(0, 0, "Choisis ta matière", {
        font: "900 52px Arial",
        fill: "#f7fbff",
        stroke: "#10203a",
        strokeThickness: 5,
        padding: { x: 40, y: 30 },
      })
      .setOrigin(0.5);

    this.subtitleText = this.add
      .text(
        0,
        0,
        "Un parcours clair pour réviser, progresser pas à pas et entrer dans l arène en confiance.",
        {
          font: "22px Arial",
          fill: "#c6d7ea",
          align: "center",
          wordWrap: { width: 680 },
        },
      )
      .setOrigin(0.5);

    this.mathCard = this._createSubjectCard({
      accent: COURSE_THEME.cyan,
      eyebrow: "LOGIQUE",
      title: "Mathématiques",
      description: "Un parcours clair pour revoir les bases, s'entraîner et gagner en assurance.",
    });

    this.backButton = this.add
      .text(0, 0, "Retour", {
        font: "bold 18px Arial",
        fill: "#ffffff",
        backgroundColor: "#1f4ed8",
        padding: { x: 18, y: 10 },
      })
      .setOrigin(0.5)
      .setInteractive({ useHandCursor: true });

    this._wireCard(this.mathCard, () => this.scene.start("MathCourseScene"));

    this.backButton.on("pointerover", () =>
      this.backButton.setStyle({ backgroundColor: "#2563eb" }),
    );
    this.backButton.on("pointerout", () =>
      this.backButton.setStyle({ backgroundColor: "#1f4ed8" }),
    );
    this.backButton.on("pointerdown", () => this.scene.start("MenuScene"));

    this.input.keyboard.on("keydown-ESC", () => this.scene.start("MenuScene"));

    this._layout(width, height);

    this.scale.on("resize", this._onResize, this);
    this.events.once("shutdown", () => {
      this.scale.off("resize", this._onResize, this);
    });
  }

  _createSubjectCard({ accent, eyebrow, title, description }) {
    const container = this.add.container(0, 0);
    const shadow = this.add.rectangle(0, 10, 10, 10, 0x000000, 0.22);
    const panel = this.add
      .rectangle(0, 0, 10, 10, COURSE_THEME.panelSoft, 0.94)
      .setStrokeStyle(2, accent, 0.95);
    const glow = this.add.rectangle(0, 0, 10, 10, accent, 0.08);
    const accentBar = this.add.rectangle(0, 0, 10, 10, accent, 1);
    const orb = this.add.circle(0, 0, 32, accent, 0.18);
    const eyebrowText = this.add.text(0, 0, eyebrow, {
      font: "900 14px Arial",
      fill: "#9cb3c9",
      letterSpacing: 2,
    });
    const titleText = this.add.text(0, 0, title, {
      font: "900 32px Arial",
      fill: "#f7fbff",
    });
    const descriptionText = this.add.text(0, 0, description, {
      font: "18px Arial",
      fill: "#dbe9f7",
      wordWrap: { width: 280 },
    });
    container.add([
      shadow,
      panel,
      glow,
      accentBar,
      orb,
      eyebrowText,
      titleText,
      descriptionText,
    ]);

    container.cardParts = {
      accent,
      shadow,
      panel,
      glow,
      accentBar,
      orb,
      eyebrowText,
      titleText,
      descriptionText,
    };

    container.setInteractive(
      new Phaser.Geom.Rectangle(-5, -5, 10, 10),
      Phaser.Geom.Rectangle.Contains,
    );

    return container;
  }

  _wireCard(card, onClick) {
    card.on("pointerover", () => this._setCardState(card, true));
    card.on("pointerout", () => this._setCardState(card, false));
    card.on("pointerdown", () => {
      const isMobile =
        this.scale.width < 900 || this.scale.height > this.scale.width;
      if (isMobile) {
        onClick();
        return;
      }

      this.tweens.add({
        targets: card,
        scaleX: 0.98,
        scaleY: 0.98,
        yoyo: true,
        duration: 90,
        onComplete: onClick,
      });
    });
  }

  _setCardState(card, isHovered) {
    const isMobile =
      this.scale.width < 900 || this.scale.height > this.scale.width;
    if (isMobile) {
      return;
    }

    const { glow, shadow, panel, accent } = card.cardParts;
    this.tweens.add({
      targets: card,
      y: isHovered ? card.baseY - 8 : card.baseY,
      scaleX: isHovered ? 1.02 : 1,
      scaleY: isHovered ? 1.02 : 1,
      duration: 180,
      ease: "Sine.Out",
    });
    this.tweens.add({
      targets: shadow,
      alpha: isHovered ? 0.3 : 0.22,
      y: isHovered ? 16 : 10,
      duration: 180,
    });
    glow.setAlpha(isHovered ? 0.14 : 0.08);
    panel.setStrokeStyle(2, accent, isHovered ? 1 : 0.95);
  }

  _onResize() {
    if (this._resizeTimer) {
      this._resizeTimer.remove();
    }

    this._resizeTimer = this.time.delayedCall(150, () => {
      const width = window.innerWidth;
      const height = window.innerHeight;
      this.scale.resize(width, height);
      this._layout(width, height);
    });
  }

  _snap(value) {
    return Math.round(value);
  }

  _drawBackground(width, height) {
    this.bgGradient.clear();
    this.bgGradient.fillGradientStyle(
      COURSE_THEME.bgTop,
      COURSE_THEME.bgTop,
      COURSE_THEME.bgBottom,
      COURSE_THEME.bgBottom,
      1,
    );
    this.bgGradient.fillRect(0, 0, width, height);

    this.bgGlow.clear();
    this.bgGlow.fillStyle(COURSE_THEME.cyan, 0.08);
    this.bgGlow.fillCircle(width * 0.18, height * 0.22, Math.min(width, height) * 0.2);
    this.bgGlow.fillStyle(COURSE_THEME.yellow, 0.06);
    this.bgGlow.fillCircle(width * 0.82, height * 0.2, Math.min(width, height) * 0.18);

    this.grid.clear();
    this.grid.lineStyle(1, COURSE_THEME.line, 0.12);
    const step = Math.max(32, Math.round(Math.min(width, height) * 0.06));
    for (let x = 0; x <= width; x += step) {
      this.grid.lineBetween(x, 0, x, height);
    }
    for (let y = 0; y <= height; y += step) {
      this.grid.lineBetween(0, y, width, y);
    }
  }

  _layout(width, height) {
    const isMobile = width < 900 || height > width;
    const base = Math.min(width, height);
    const heroWidth = Math.min(width * (isMobile ? 0.9 : 0.82), 980);
    const heroHeight = Math.min(height * (isMobile ? 0.18 : 0.24), isMobile ? 140 : 220);
    const cardWidth = Math.min(isMobile ? width * 0.92 : width * 0.34, 420);
    const cardHeight = isMobile ? 182 : 210;
    const titleSize = Phaser.Math.Clamp(
      Math.round(base * (isMobile ? 0.11 : 0.085)),
      32,
      70,
    );
    const subtitleSize = Phaser.Math.Clamp(
      Math.round(base * (isMobile ? 0.03 : 0.028)),
      14,
      24,
    );
    const cardTitleSize = Phaser.Math.Clamp(
      Math.round(base * (isMobile ? 0.05 : 0.042)),
      24,
      34,
    );
    const cardTextSize = Phaser.Math.Clamp(
      Math.round(base * (isMobile ? 0.026 : 0.024)),
      14,
      20,
    );

    this._drawBackground(width, height);

    this.heroShadow
      .setPosition(this._snap(width / 2 + 8), this._snap(height * (isMobile ? 0.18 : 0.22) + 10))
      .setSize(heroWidth, heroHeight);
    this.heroPanel
      .setPosition(this._snap(width / 2), this._snap(height * (isMobile ? 0.18 : 0.22)))
      .setSize(heroWidth, heroHeight);

    this.titleText
      .setPosition(this._snap(width / 2), this._snap(height * (isMobile ? 0.15 : 0.2)))
      .setStyle({ font: `900 ${titleSize}px Arial` });
    this.subtitleText
      .setPosition(this._snap(width / 2), this._snap(height * (isMobile ? 0.21 : 0.27)))
      .setStyle({
        font: `${subtitleSize}px Arial`,
        wordWrap: { width: Math.min(heroWidth - 40, 700) },
      });

    const cardY = isMobile ? height * 0.54 : height * 0.58;
    const positions = [{ x: width / 2, y: cardY }];

    [this.mathCard].forEach((card, index) => {
      const {
        shadow,
        panel,
        glow,
        accentBar,
        orb,
        eyebrowText,
        titleText,
        descriptionText,
      } = card.cardParts;

      const { x, y } = positions[index];
      card.setPosition(this._snap(x), this._snap(y));
      card.baseY = this._snap(y);
      card.input.hitArea.setTo(-(cardWidth / 2), -(cardHeight / 2), cardWidth, cardHeight);
      card.input.hitAreaCallback = Phaser.Geom.Rectangle.Contains;

      shadow.setSize(cardWidth, cardHeight);
      panel.setSize(cardWidth, cardHeight);
      glow.setSize(cardWidth - 8, cardHeight - 8);
      accentBar
        .setSize(isMobile ? 8 : 10, cardHeight - 28)
        .setPosition(this._snap(-(cardWidth / 2) + 18), 0);
      orb.setPosition(
        this._snap(cardWidth / 2 - (isMobile ? 34 : 46)),
        this._snap(-(cardHeight / 2) + (isMobile ? 34 : 44)),
      );
      eyebrowText
        .setPosition(
          this._snap(-(cardWidth / 2) + 30),
          this._snap(-(cardHeight / 2) + 18),
        )
        .setStyle({
          font: `900 ${Math.max(12, Math.round(cardTextSize * 0.75))}px Arial`,
        });
      titleText
        .setPosition(
          this._snap(-(cardWidth / 2) + 30),
          this._snap(-(cardHeight / 2) + (isMobile ? 50 : 42)),
        )
        .setStyle({ font: `900 ${cardTitleSize}px Arial` });
      descriptionText
        .setPosition(
          this._snap(-(cardWidth / 2) + 30),
          this._snap(-(cardHeight / 2) + (isMobile ? 90 : 74)),
        )
        .setStyle({
          font: `${cardTextSize}px Arial`,
          wordWrap: { width: cardWidth - (isMobile ? 72 : 96) },
        });
    });

    this.backButton
      .setPosition(this._snap(width / 2), this._snap(height * (isMobile ? 0.9 : 0.84)))
      .setStyle({
        font: `bold ${Math.max(isMobile ? 18 : 16, Math.round(base * 0.03))}px Arial`,
        padding: { x: isMobile ? 22 : 18, y: isMobile ? 12 : 10 },
      });
  }
}
