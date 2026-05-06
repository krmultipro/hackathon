import { getGlobalLeaderboard } from "../modules/leaderboard/leaderboardService.js";

const MENU_THEME = {
  bgTop: 0x08101c,
  bgBottom: 0x170b16,
  cyan: 0x56ccf2,
  teal: 0x56ccf2,
  yellow: 0xf2c94c,
  coral: 0xff7675,
  white: 0xf7fbff,
  slate: 0x9cb3c9,
  panel: 0x101626,
  panelDeep: 0x0d1322,
  line: 0x334766,
};

export default class MenuScene extends Phaser.Scene {
  constructor() {
    super({ key: "MenuScene" });
  }

  create() {
    const { width, height } = this.scale;

    this._createBackground(width, height);
    this._createHero();
    this._createStatChips();
    this._createMenuCards();
    this._createTopActions();
    this._createLeaderboardModal(width, height);
    this._attachEvents();
    this._startAmbientMotion();
    this._layout(width, height);

    this.scale.on("resize", this._onResize, this);
    this.events.once("shutdown", () => {
      this.scale.off("resize", this._onResize, this);
    });
  }

  _createBackground(width, height) {
    this.bgGradient = this.add.graphics();
    this.bgGlow = this.add.graphics();
    this.grid = this.add.graphics();
    this.orbLeft = this.add.circle(0, 0, 120, MENU_THEME.cyan, 0.16);
    this.orbRight = this.add.circle(0, 0, 160, MENU_THEME.coral, 0.14);
    this.orbBottom = this.add.circle(0, 0, 180, MENU_THEME.yellow, 0.08);
    this.heroPanel = this.add
      .rectangle(0, 0, 10, 10, MENU_THEME.panel, 0.76)
      .setStrokeStyle(2, MENU_THEME.line, 0.55);
    this.heroPanelShadow = this.add.rectangle(0, 0, 10, 10, 0x000000, 0.18);
  }

  _snap(value) {
    return Math.round(value);
  }

  _createHero() {
    this.titleText = this.add
      .text(0, 0, "EducArena", {
        font: "900 72px Arial",
        fill: "#f7fbff",
        stroke: "#10203a",
        strokeThickness: 6,
      })
      .setOrigin(0.5);

    this.subtitleText = this.add
      .text(
        0,
        0,
        "Releve des duels eclairs, monte en ELO et transforme tes cours en terrain de jeu.",
        {
          font: "26px Arial",
          fill: "#d6e6f5",
          align: "center",
          wordWrap: { width: 760 },
        },
      )
      .setOrigin(0.5);

    this.heroHint = this.add
      .text(0, 0, "Choisis ton defi du jour", {
        font: "bold 20px Arial",
        fill: "#f2c94c",
      })
      .setOrigin(0.5);
  }

  _createStatChips() {
    this.statChips = [
      this._createChip("Quiz rapides", MENU_THEME.cyan),
      this._createChip("Duel arena", MENU_THEME.coral),
      this._createChip("Progression ELO", MENU_THEME.yellow),
    ];
  }

  _createChip(label, color) {
    const bg = this.add
      .rectangle(0, 0, 10, 10, 0xffffff, 0.08)
      .setStrokeStyle(1, color, 0.65);
    const dot = this.add.circle(0, 0, 6, color, 1);
    const text = this.add
      .text(0, 0, label, {
        font: "bold 18px Arial",
        fill: "#f7fbff",
      })
      .setOrigin(0.5);

    return { bg, dot, text };
  }

  _createMenuCards() {
    this.courseCard = this._createActionCard({
      accent: MENU_THEME.cyan,
      eyebrow: "APPRENDRE",
      title: "Cours",
      description:
        "Des parcours clairs pour reviser, comprendre et progresser pas a pas.",
    });
    this.tournamentCard = this._createActionCard({
      accent: MENU_THEME.coral,
      eyebrow: "AFFRONTER",
      title: "Tournoi",
      description:
        "Des duels rapides pour tester tes acquis et gagner en niveau.",
    });
    this.leaderboardCard = this._createActionCard({
      accent: MENU_THEME.yellow,
      eyebrow: "BRILLER",
      title: "Classement",
      description:
        "Suis ta progression, compare tes scores et vise la premiere place.",
    });
  }

  _createActionCard({ accent, eyebrow, title, description }) {
    const container = this.add.container(0, 0);
    const shadow = this.add.rectangle(0, 10, 10, 10, 0x000000, 0.22);
    const panel = this.add
      .rectangle(0, 0, 10, 10, MENU_THEME.panelDeep, 0.92)
      .setStrokeStyle(2, accent, 0.9);
    const glow = this.add.rectangle(0, 0, 10, 10, accent, 0.08);
    const accentBar = this.add.rectangle(0, 0, 10, 10, accent, 1);
    const orb = this.add.circle(0, 0, 34, accent, 0.18);
    const eyebrowText = this.add.text(0, 0, eyebrow, {
      font: "900 16px Arial",
      fill: "#9cb3c9",
      letterSpacing: 2,
    });
    const titleText = this.add.text(0, 0, title, {
      font: "900 34px Arial",
      fill: "#f7fbff",
    });
    const descriptionText = this.add.text(0, 0, description, {
      font: "20px Arial",
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
    container.setSize(10, 10);
    container.setInteractive(
      new Phaser.Geom.Rectangle(-5, -5, 10, 10),
      Phaser.Geom.Rectangle.Contains,
    );

    container.cardParts = {
      title,
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

    return container;
  }

  _createTopActions() {
    this.logoutButton = this.add
      .text(0, 0, "Deconnexion", {
        font: "bold 18px Arial",
        fill: "#ffd7d1",
        backgroundColor: "#4a1820",
        padding: { x: 12, y: 8 },
      })
      .setOrigin(1, 0)
      .setInteractive({ useHandCursor: true });

    this.cornerLabel = this.add
      .text(0, 0, "College Battle Mode", {
        font: "bold 18px Arial",
        fill: "#c3d6ea",
      })
      .setOrigin(0, 0);
  }

  _createLeaderboardModal(width, height) {
    this.leaderboardOverlay = this.add
      .rectangle(0, 0, width, height, 0x03060f, 0.82)
      .setOrigin(0)
      .setDepth(40)
      .setVisible(false)
      .setInteractive();
    this.leaderboardPanel = this.add
      .rectangle(0, 0, 10, 10, MENU_THEME.panelDeep, 0.97)
      .setStrokeStyle(2, MENU_THEME.yellow, 0.9)
      .setDepth(41)
      .setVisible(false);
    this.leaderboardGlow = this.add
      .rectangle(0, 0, 10, 10, MENU_THEME.yellow, 0.08)
      .setDepth(41)
      .setVisible(false);
    this.leaderboardTitle = this.add
      .text(0, 0, "Classement", {
        font: "900 38px Arial",
        fill: "#f7fbff",
      })
      .setOrigin(0.5)
      .setDepth(42)
      .setVisible(false);
    this.leaderboardSubtitle = this.add
      .text(0, 0, "Top joueurs", {
        font: "bold 18px Arial",
        fill: "#f2c94c",
      })
      .setOrigin(0.5)
      .setDepth(42)
      .setVisible(false);
    this.leaderboardContent = this.add
      .text(0, 0, "", {
        font: "22px Arial",
        fill: "#dbe9f7",
        align: "left",
        wordWrap: { width: 420 },
      })
      .setOrigin(0.5, 0)
      .setDepth(42)
      .setVisible(false);
    this.closeLeaderboardButton = this.add
      .text(0, 0, "Fermer", {
        font: "bold 20px Arial",
        fill: "#091321",
        backgroundColor: "#49d6ff",
        padding: { x: 16, y: 10 },
      })
      .setOrigin(0.5)
      .setDepth(42)
      .setVisible(false)
      .setInteractive({ useHandCursor: true });
  }

  _attachEvents() {
    this._wireCard(this.courseCard, () => this.scene.start("CourseScene"));
    this._wireCard(this.tournamentCard, () => {
      this.scene.start("CharacterSelectScene", { previousScene: "MenuScene" });
    });
    this._wireCard(this.leaderboardCard, () => this.openLeaderboard());

    this.logoutButton.on("pointerover", () =>
      this.logoutButton.setStyle({ fill: "#ffffff" }),
    );
    this.logoutButton.on("pointerout", () =>
      this.logoutButton.setStyle({ fill: "#ffd7d1" }),
    );
    this.logoutButton.on("pointerdown", () => {
      if (typeof window.logout === "function") {
        window.logout();
      }
    });

    this.leaderboardOverlay.on("pointerdown", () => this.closeLeaderboard());
    this.closeLeaderboardButton.on("pointerdown", () =>
      this.closeLeaderboard(),
    );
  }

  _wireCard(card, onClick) {
    card.on("pointerover", () => this._setCardState(card, true));
    card.on("pointerout", () => this._setCardState(card, false));
    card.on("pointerdown", () => {
      const isMobile =
        this.scale.width < 960 || this.scale.height > this.scale.width;
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
      this.scale.width < 960 || this.scale.height > this.scale.width;
    if (isMobile) {
      return;
    }

    const { glow, shadow, panel } = card.cardParts;
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
    panel.setStrokeStyle(2, card.cardParts.accent, isHovered ? 1 : 0.9);
    glow.setAlpha(isHovered ? 0.14 : 0.08);
  }

  _startAmbientMotion() {
    [this.orbLeft, this.orbRight, this.orbBottom].forEach((orb, index) => {
      this.tweens.add({
        targets: orb,
        alpha: { from: orb.alpha, to: orb.alpha * 0.55 },
        scaleX: 1.08,
        scaleY: 1.08,
        yoyo: true,
        repeat: -1,
        duration: 2200 + index * 500,
        ease: "Sine.InOut",
      });
    });

    if (this.scale.width < 960 || this.scale.height > this.scale.width) {
      return;
    }

    [this.courseCard, this.tournamentCard, this.leaderboardCard].forEach(
      (card, index) => {
        this.tweens.add({
          targets: card,
          y: `-=${2 + index}`,
          duration: 1700 + index * 250,
          yoyo: true,
          repeat: -1,
          ease: "Sine.InOut",
        });
      },
    );
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

  _layout(width, height) {
    const isMobile = width < 960 || height > width;
    const base = Math.min(width, height);
    const heroWidth = Math.min(width * (isMobile ? 0.9 : 0.84), 1180);
    const heroHeight = Math.min(
      height * (isMobile ? 0.18 : 0.3),
      isMobile ? 132 : 270,
    );
    const cardWidth = Math.min(isMobile ? width * 0.93 : width * 0.25, 340);
    const cardHeight = isMobile ? 176 : 220;
    const titleSize = Phaser.Math.Clamp(
      Math.round(base * (isMobile ? 0.13 : 0.095)),
      34,
      84,
    );
    const subtitleSize = Phaser.Math.Clamp(
      Math.round(base * (isMobile ? 0.034 : 0.03)),
      13,
      26,
    );
    const cardTitleSize = Phaser.Math.Clamp(
      Math.round(base * (isMobile ? 0.062 : 0.04)),
      24,
      36,
    );
    const cardTextSize = Phaser.Math.Clamp(
      Math.round(base * (isMobile ? 0.027 : 0.024)),
      13,
      20,
    );
    const chipTextSize = Phaser.Math.Clamp(
      Math.round(base * (isMobile ? 0.027 : 0.022)),
      13,
      18,
    );
    const modalWidth = Math.min(width * 0.84, 620);
    const modalHeight = Math.min(
      height * (isMobile ? 0.92 : 0.74),
      isMobile ? 760 : 560,
    );

    this._drawBackground(width, height);

    this.heroPanelShadow
      .setPosition(
        this._snap(width / 2 + 8),
        this._snap(height * (isMobile ? 0.19 : 0.26) + 10),
      )
      .setSize(heroWidth, heroHeight);
    this.heroPanel
      .setPosition(
        this._snap(width / 2),
        this._snap(height * (isMobile ? 0.19 : 0.26)),
      )
      .setSize(heroWidth, heroHeight);

    this.titleText
      .setPosition(
        this._snap(width / 2),
        this._snap(height * (isMobile ? 0.16 : 0.22)),
      )
      .setStyle({ font: `900 ${titleSize}px Arial` });
    this.subtitleText
      .setPosition(
        this._snap(width / 2),
        this._snap(height * (isMobile ? 0.215 : 0.285)),
      )
      .setStyle({
        font: `${subtitleSize}px Arial`,
        wordWrap: { width: Math.min(heroWidth - (isMobile ? 36 : 80), 760) },
      });
    this.heroHint
      .setPosition(
        this._snap(width / 2),
        this._snap(height * (isMobile ? 0.285 : 0.35)),
      )
      .setStyle({
        font: `bold ${Math.max(isMobile ? 12 : 16, Math.round(base * 0.026))}px Arial`,
      });

    this._layoutChips(width, height, isMobile, chipTextSize);
    this._layoutCards(
      width,
      height,
      isMobile,
      cardWidth,
      cardHeight,
      cardTitleSize,
      cardTextSize,
    );

    this.logoutButton
      .setPosition(this._snap(width - 18), this._snap(isMobile ? 16 : 20))
      .setStyle({
        font: `bold ${Math.max(isMobile ? 12 : 14, Math.round(base * 0.023))}px Arial`,
      });
    this.cornerLabel
      .setPosition(this._snap(18), this._snap(isMobile ? 18 : 24))
      .setStyle({
        font: `bold ${Math.max(isMobile ? 12 : 14, Math.round(base * 0.022))}px Arial`,
      });
    this.cornerLabel.setVisible(!isMobile);

    this._layoutLeaderboardModal(width, height, modalWidth, modalHeight, base);
  }

  _drawBackground(width, height) {
    this.bgGradient.clear();
    this.bgGradient.fillGradientStyle(
      MENU_THEME.bgTop,
      MENU_THEME.bgTop,
      MENU_THEME.bgBottom,
      MENU_THEME.bgBottom,
      1,
    );
    this.bgGradient.fillRect(0, 0, width, height);

    this.bgGlow.clear();
    this.bgGlow.fillStyle(MENU_THEME.cyan, 0.09);
    this.bgGlow.fillCircle(
      width * 0.18,
      height * 0.2,
      Math.min(width, height) * 0.2,
    );
    this.bgGlow.fillStyle(MENU_THEME.coral, 0.09);
    this.bgGlow.fillCircle(
      width * 0.84,
      height * 0.18,
      Math.min(width, height) * 0.16,
    );
    this.bgGlow.fillStyle(MENU_THEME.yellow, 0.04);
    this.bgGlow.fillCircle(
      width * 0.5,
      height * 0.82,
      Math.min(width, height) * 0.2,
    );

    this.grid.clear();
    this.grid.lineStyle(1, MENU_THEME.line, 0.12);
    const step = Math.max(32, Math.round(Math.min(width, height) * 0.06));
    for (let x = 0; x <= width; x += step) {
      this.grid.lineBetween(x, 0, x, height);
    }
    for (let y = 0; y <= height; y += step) {
      this.grid.lineBetween(0, y, width, y);
    }

    this.orbLeft.setPosition(width * 0.14, height * 0.22);
    this.orbRight.setPosition(width * 0.82, height * 0.16);
    this.orbBottom.setPosition(width * 0.7, height * 0.84);
  }

  _layoutChips(width, height, isMobile, chipTextSize) {
    if (isMobile) {
      this.statChips.forEach((chip) => {
        chip.bg.setVisible(false);
        chip.dot.setVisible(false);
        chip.text.setVisible(false);
      });
      return;
    }

    const chipY = isMobile ? height * 0.395 : height * 0.4;
    const chipGap = isMobile ? 8 : 18;

    this.statChips.forEach((chip, index) => {
      chip.bg.setVisible(true);
      chip.dot.setVisible(true);
      chip.text.setVisible(true);
      const chipWidth = isMobile ? Math.min(width * 0.28, 120) : 220;
      const chipHeight = isMobile ? 28 : 42;
      const x = isMobile
        ? width / 2 + (index - 1) * (chipWidth + chipGap)
        : width / 2 + (index - 1) * (chipWidth + chipGap);
      const y = isMobile ? chipY : chipY;

      chip.bg
        .setPosition(this._snap(x), this._snap(y))
        .setSize(chipWidth, chipHeight);
      chip.dot.setPosition(
        this._snap(x - chipWidth / 2 + (isMobile ? 12 : 22)),
        this._snap(y),
      );
      chip.text
        .setPosition(this._snap(x + (isMobile ? 4 : 8)), this._snap(y))
        .setStyle({
          font: `bold ${isMobile ? Math.max(10, chipTextSize - 3) : chipTextSize}px Arial`,
        });
    });
  }

  _layoutCards(
    width,
    height,
    isMobile,
    cardWidth,
    cardHeight,
    cardTitleSize,
    cardTextSize,
  ) {
    const cardY = isMobile ? height * 0.66 : height * 0.68;
    const spacing = isMobile ? 206 : cardWidth + 26;

    const positions = isMobile
      ? [
          { x: width / 2, y: cardY - 210 },
          { x: width / 2, y: cardY + 4 },
          { x: width / 2, y: cardY + 218 },
        ]
      : [
          { x: width / 2 - spacing, y: cardY },
          { x: width / 2, y: cardY - 18 },
          { x: width / 2 + spacing, y: cardY },
        ];

    [this.courseCard, this.tournamentCard, this.leaderboardCard].forEach(
      (card, index) => {
        const {
          shadow,
          panel,
          glow,
          accentBar,
          orb,
          eyebrowText,
          titleText,
          descriptionText,
          title,
        } = card.cardParts;
        const { x, y } = positions[index];
        card.setPosition(this._snap(x), this._snap(y));
        card.baseY = this._snap(y);
        card.input.hitArea.setTo(
          -(cardWidth / 2),
          -(cardHeight / 2),
          cardWidth,
          cardHeight,
        );
        card.input.hitAreaCallback = Phaser.Geom.Rectangle.Contains;
        shadow.setSize(cardWidth, cardHeight);
        panel.setSize(cardWidth, cardHeight);
        glow.setSize(cardWidth - 8, cardHeight - 8);
        accentBar
          .setSize(isMobile ? 8 : 10, cardHeight - (isMobile ? 26 : 36))
          .setPosition(this._snap(-(cardWidth / 2) + (isMobile ? 18 : 22)), 0);
        orb.setPosition(
          this._snap(cardWidth / 2 - (isMobile ? 32 : 52)),
          this._snap(-(cardHeight / 2) + (isMobile ? 34 : 52)),
        );
        eyebrowText
          .setPosition(
            this._snap(-(cardWidth / 2) + (isMobile ? 28 : 34)),
            this._snap(-(cardHeight / 2) + (isMobile ? 16 : 28)),
          )
          .setStyle({
            font: `900 ${Math.max(isMobile ? 10 : 12, Math.round(cardTextSize * 0.75))}px Arial`,
          });
        titleText
          .setPosition(
            this._snap(-(cardWidth / 2) + (isMobile ? 28 : 34)),
            this._snap(-(cardHeight / 2) + (isMobile ? 48 : 58)),
          )
          .setStyle({
            font: `900 ${Math.max(isMobile && title === "Classement" ? cardTitleSize - 2 : cardTitleSize, 20)}px Arial`,
          });
        descriptionText
          .setPosition(
            this._snap(-(cardWidth / 2) + (isMobile ? 28 : 34)),
            this._snap(
              -(cardHeight / 2) +
                (isMobile ? 90 : 108),
            ),
          )
          .setStyle({
            font: `${Math.max(isMobile && title === "Classement" ? cardTextSize - 1 : cardTextSize, 10)}px Arial`,
            wordWrap: {
              width: cardWidth - (isMobile ? 68 : 86),
            },
          });
      },
    );
  }

  _layoutLeaderboardModal(width, height, modalWidth, modalHeight, base) {
    const isMobile = width < 960 || height > width;
    this.leaderboardOverlay.setSize(width, height);
    this.leaderboardPanel
      .setPosition(this._snap(width / 2), this._snap(height / 2))
      .setSize(modalWidth, modalHeight);
    this.leaderboardGlow
      .setPosition(this._snap(width / 2), this._snap(height / 2))
      .setSize(modalWidth - 10, modalHeight - 10);
    this.leaderboardTitle
      .setPosition(
        this._snap(width / 2),
        this._snap(height / 2 - modalHeight * (isMobile ? 0.32 : 0.36)),
      )
      .setStyle({
        font: `900 ${Math.max(isMobile ? 24 : 28, Math.round(base * 0.05))}px Arial`,
      });
    this.leaderboardSubtitle
      .setPosition(
        this._snap(width / 2),
        this._snap(height / 2 - modalHeight * (isMobile ? 0.24 : 0.27)),
      )
      .setStyle({
        font: `bold ${Math.max(isMobile ? 14 : 15, Math.round(base * 0.022))}px Arial`,
      });
    this.leaderboardContent
      .setPosition(
        this._snap(width / 2),
        this._snap(height / 2 - modalHeight * (isMobile ? 0.15 : 0.18)),
      )
      .setStyle({
        font: `${Math.max(isMobile ? 15 : 16, Math.round(base * 0.03))}px Arial`,
        wordWrap: { width: modalWidth - (isMobile ? 64 : 90) },
      });
    this.closeLeaderboardButton
      .setPosition(
        this._snap(width / 2),
        this._snap(height / 2 + modalHeight * (isMobile ? 0.42 : 0.39)),
      )
      .setStyle({
        font: `bold ${Math.max(isMobile ? 15 : 16, Math.round(base * 0.024))}px Arial`,
      });
  }

  async openLeaderboard() {
    this._setLeaderboardVisibility(true);
    this.leaderboardContent.setText("Chargement du classement...");

    try {
      const players = await getGlobalLeaderboard();

      if (!players.length) {
        this.leaderboardContent.setText("Aucun joueur classe pour le moment.");
        return;
      }

      const lines = players.map((player, index) => {
        const prefix =
          index === 0 ? "1. Champion" : `${player.rank || index + 1}.`;
        return `${prefix}  ${player.username}  -  ${player.globalElo || 0} ELO`;
      });

      this.leaderboardContent.setText(lines.join("\n\n"));
    } catch (error) {
      this.leaderboardContent.setText(
        error.message || "Impossible de charger le classement.",
      );
    }
  }

  closeLeaderboard() {
    this._setLeaderboardVisibility(false);
  }

  _setLeaderboardVisibility(isVisible) {
    this.leaderboardOverlay.setVisible(isVisible);
    this.leaderboardPanel.setVisible(isVisible);
    this.leaderboardGlow.setVisible(isVisible);
    this.leaderboardTitle.setVisible(isVisible);
    this.leaderboardSubtitle.setVisible(isVisible);
    this.leaderboardContent.setVisible(isVisible);
    this.closeLeaderboardButton.setVisible(isVisible);
  }
}
