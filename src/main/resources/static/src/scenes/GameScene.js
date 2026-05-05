export default class GameScene extends Phaser.Scene {
    constructor() {
        super({ key: 'GameScene' });
        this.maxHp = 10;
        this.timePerQuestion = 30;
    }

    init(data) {
        this.p1char = (data && data.p1char) ? data.p1char : 'boy';
        this.p2char = (data && data.p2char) ? data.p2char : 'girl';
        this.subject = (data && data.subject) ? data.subject : 'math';
        this.previousScene = (data && data.previousScene) ? data.previousScene : 'CharacterSelectScene';
        this.characterSelectPreviousScene = (data && data.characterSelectPreviousScene) ? data.characterSelectPreviousScene : 'MenuScene';
    }

    create() {
        const { width, height } = this.scale;

        this.bg = this.add.rectangle(0, 0, 1, 1, 0x101626).setOrigin(0);

        const p1tex = this.p1char === 'boy' ? 'p1boy' : 'p1girl';
        const p2tex = this.p2char === 'boy' ? 'p2boy' : 'p2girl';

        this.leftFighter = this.add.image(0, 0, p1tex);
        this.rightFighter = this.add.image(0, 0, p2tex).setFlipX(true);

        this.leftHp = this.maxHp;
        this.rightHp = this.maxHp;
        this.timeLeft = this.timePerQuestion;
        this.roundLocked = false;
        this.leftAnswer = null;
        this.rightAnswer = null;

        const mathQuestions = [
            { q: 'Combien font 7 x 8 ?', choices: ['54', '56', '58', '64'], a: 1 },
            { q: 'Quelle est la racine carree de 81 ?', choices: ['7', '8', '9', '10'], a: 2 },
            { q: 'Combien font 15 - 9 ?', choices: ['5', '6', '7', '8'], a: 1 },
            { q: 'Quelle fraction est equivalente a 1/2 ?', choices: ['2/3', '3/6', '4/10', '5/8'], a: 1 },
            { q: 'Combien font 12 + 13 ?', choices: ['23', '24', '25', '26'], a: 2 },
            { q: 'Combien font 6 x 6 ?', choices: ['30', '32', '34', '36'], a: 3 }
        ];

        const frenchQuestions = [
            { q: 'Quel est le pluriel de cheval ?', choices: ['chevals', 'chevaux', 'chevaus', 'chevails'], a: 1 },
            { q: 'Choisis le verbe correctement conjugue.', choices: ['Nous mange', 'Nous manges', 'Nous mangeons', 'Nous manger'], a: 2 },
            { q: 'Quel mot est un adjectif ?', choices: ['courir', 'rapidement', 'bleu', 'maison'], a: 2 },
            { q: 'Quel est le contraire de heureux ?', choices: ['content', 'triste', 'joyeux', 'souriant'], a: 1 },
            { q: 'Dans "Les enfants jouent", le sujet est :', choices: ['jouent', 'enfants', 'Les enfants', 'les'], a: 2 },
            { q: 'Quel mot est bien orthographie ?', choices: ['apparament', 'apparement', 'apparemment', 'aparament'], a: 2 }
        ];

        this.questions = this.subject === 'fr' ? frenchQuestions : mathQuestions;

        this.turnText = this.add.text(0, 0, 'Mode simultane', {
            font: '24px Arial',
            fill: '#f2c94c'
        }).setOrigin(0.5);

        this.timerBarBg  = this.add.graphics();
        this.timerBar    = this.add.graphics();
        this.timerBarGlow = this.add.graphics().setBlendMode(Phaser.BlendModes.ADD);

        this.leftHpGfx  = this.add.graphics();
        this.rightHpGfx = this.add.graphics();

        this.sceneBg = this.add.image(0, 0, 'sceneBg').setAlpha(0.2).setDepth(0);

        this.questionText = this.add.text(0, 0, '', {
            font: '26px Arial',
            fill: '#ffffff',
            align: 'center',
            wordWrap: { width: 400 }
        }).setOrigin(0.5, 0);

        this.choiceTexts = [];
        for (let i = 0; i < 4; i += 1) {
            const choiceText = this.add.text(0, 0, '', {
                font: '22px Arial',
                fill: '#dfe6e9'
            }).setOrigin(0.5);
            this.choiceTexts.push(choiceText);
        }

        this.feedbackText = this.add.text(0, 0, '', {
            font: '20px Arial',
            fill: '#ffffff',
            align: 'center',
            wordWrap: { width: 400 }
        }).setOrigin(0.5);

        this.helpText = this.add.text(0, 0, 'Touchez les boutons ci-dessous', {
            font: '16px Arial',
            fill: '#aaaaaa'
        }).setOrigin(0.5);

        this.backButton = this.add.text(0, 0, '[ RETOUR ]', {
            font: '18px Arial',
            fill: '#aaaaaa'
        }).setOrigin(0, 0.5).setInteractive({ useHandCursor: true });

        this.backButton.on('pointerover', () => this.backButton.setStyle({ fill: '#ffffff' }));
        this.backButton.on('pointerout', () => this.backButton.setStyle({ fill: '#aaaaaa' }));
        this.backButton.on('pointerdown', () => this.scene.start(this.previousScene, {
            subject: this.subject,
            previousScene: this.characterSelectPreviousScene
        }));

        this.keys = this.input.keyboard.addKeys({
            esc: Phaser.Input.Keyboard.KeyCodes.ESC
        });

        // Retour page précédente (Echap)
        this.input.keyboard.on('keydown-ESC', () => this.scene.start(this.previousScene, {
            subject: this.subject,
            previousScene: this.characterSelectPreviousScene
        }));
        this.escText = this.add.text(0, 0, '[ESC] Retour', {
            font: '14px Arial',
            fill: '#aaaaaa'
        }).setOrigin(1, 0).setScrollFactor(0);

        this.timerEvent = this.time.addEvent({
            delay: 1000,
            loop: true,
            callback: this.onTick,
            callbackScope: this
        });

        this._createAnswerButtons(width, height);
        this._layout(width, height);
        this.startRound();

        this.scale.on('resize', this._onResize, this);
        this.events.once('shutdown', () => {
            this.scale.off('resize', this._onResize, this);
        });
    }

    _onResize(gameSize) {
        if (this._resizeTimer) { this._resizeTimer.remove(); }
        this._resizeTimer = this.time.delayedCall(150, () => {
            const width  = window.innerWidth;
            const height = window.innerHeight;
            this.scale.resize(width, height);
            this._createAnswerButtons(width, height);
            this._layout(width, height);
            this.updateUi();
        });
    }

    _layout(width, height) {
        const isMobile = width < 900 || height > width;
        const base = Math.min(width, height);
        const fs = (n, min, max) => `${Phaser.Math.Clamp(Math.round(n * base / 600), min, max)}px Arial`;

        this.bg.setSize(width, height);

        const hSize = Phaser.Math.Clamp(Math.round(base * 0.033), 12, 22);
        this._heartSize = hSize;
        this.leftHpGfx.setPosition(12, 10);
        this.rightHpGfx.setPosition(width - 12, 10);
        this._drawHearts(this.leftHpGfx,  this.leftHp,  this.maxHp, hSize, false);
        this._drawHearts(this.rightHpGfx, this.rightHp, this.maxHp, hSize, true);

        const barW = width * 0.5;
        const barH = Phaser.Math.Clamp(Math.round(height * 0.028), 12, 22);
        const barY = height * 0.165;
        const turnY = Math.round((10 + hSize + barY) / 2);
        this.turnText.setPosition(width / 2, turnY).setStyle({ font: fs(24, 14, 30) });
        this._timerBarW = barW;
        this._timerBarH = barH;
        this._timerBarY = barY;
        this._timerBarX = width / 2;
        // Fond arrondi
        const r = barH / 2;
        this.timerBarBg.clear();
        this.timerBarBg.fillStyle(0x0a0a1a, 0.7);
        this.timerBarBg.fillRoundedRect(width / 2 - barW / 2 - 2, barY - barH / 2 - 2, barW + 4, barH + 4, r + 1);
        this.timerBarBg.lineStyle(1.5, 0x444477, 1);
        this.timerBarBg.strokeRoundedRect(width / 2 - barW / 2 - 2, barY - barH / 2 - 2, barW + 4, barH + 4, r + 1);
        this.escText.setPosition(width - 12, height - 24).setStyle({ font: fs(14, 10, 18) });

        const targetCharH = Phaser.Math.Clamp(Math.round((isMobile ? 0.28 : 0.42) * height), 130, 320);
        const leftImg = this.leftFighter.texture.getSourceImage();
        const rightImg = this.rightFighter.texture.getSourceImage();
        const scaleL = targetCharH / leftImg.height;
        const scaleR = targetCharH / rightImg.height;

        // Calcul de la position des boutons pour aligner les personnages au-dessus
        const btnPad = 10;
        const btnH = Math.min(isMobile ? 72 : 68, height * (isMobile ? 0.09 : 0.1));
        const gridH = 2 * btnH + 3 * btnPad;
        const gridTop = height - gridH - btnPad;
        const sideW = width * (isMobile ? 0.46 : 0.22);

        const fighterY = gridTop - targetCharH / 2 - 28;
        const fighterXLeft  = sideW / 2;
        const fighterXRight = width - sideW / 2;

        this.leftFighter.setPosition(fighterXLeft, fighterY).setScale(scaleL);
        this.rightFighter.setPosition(fighterXRight, fighterY).setScale(scaleR).setFlipX(true);

        const blockTop = isMobile ? height * 0.34 : height * 0.28;
        const blockWidth = isMobile ? width * 0.8 : width * 0.5;

        this.sceneBg.setPosition(width / 2, height / 2);
        const bgScaleW = width  / this.sceneBg.width;
        const bgScaleH = height / this.sceneBg.height;
        const bgScale  = Math.max(bgScaleW, bgScaleH);
        this.sceneBg.setScale(bgScale);

        this.questionText
            .setPosition(width / 2, blockTop)
            .setStyle({ font: fs(26, 15, 32) })
            .setWordWrapWidth(blockWidth);

        const gap = isMobile ? 32 : 42;
        for (let i = 0; i < 4; i += 1) {
            this.choiceTexts[i]
                .setPosition(width / 2, blockTop + 62 + i * gap)
                .setStyle({ font: fs(22, 14, 28) });
        }

        this.feedbackText
            .setPosition(width / 2, blockTop + (isMobile ? 200 : 250))
            .setStyle({ font: fs(20, 12, 24) })
            .setWordWrapWidth(blockWidth);

        this.helpText
            .setPosition(width / 2, blockTop + (isMobile ? 230 : 300))
            .setStyle({ font: fs(16, 10, 20) });

        this.backButton
            .setPosition(12, height - 20)
            .setStyle({ font: fs(16, 11, 20) });
    }

    update() {
        if (this.roundLocked) {
            return;
        }
    }

    onTick() {
        if (this.roundLocked) {
            return;
        }

        this.timeLeft -= 1;
        this.updateUi();

        if (this.timeLeft <= 0) {
            this.resolveRound();
        }
    }

    startRound() {
        this.roundLocked = false;
        this.timeLeft = this.timePerQuestion;
        this.leftAnswer = null;
        this.rightAnswer = null;
        this.currentQuestion = Phaser.Utils.Array.GetRandom(this.questions);
        this.feedbackText.setText('');
        this._resetButtonStates();
        this.updateUi();
    }

    updateUi() {
        const leftState  = this.leftAnswer  === null ? 'en attente' : 'valide';
        const rightState = this.rightAnswer === null ? 'en attente' : 'valide';
        this.turnText.setText(`J1: ${leftState}  |  J2: ${rightState}`);
        this._drawHearts(this.leftHpGfx,  this.leftHp,  this.maxHp, this._heartSize || 16, false);
        this._drawHearts(this.rightHpGfx, this.rightHp, this.maxHp, this._heartSize || 16, true);

        const ratio = this.timeLeft / this.timePerQuestion;
        const fullW = this._timerBarW || 200;
        const barH  = this._timerBarH || 16;
        const barY  = this._timerBarY || 100;
        const barX  = this._timerBarX || (this.scale.width / 2);
        const filledW = Math.max(0, fullW * ratio);
        const r = barH / 2;
        const color   = ratio > 0.5 ? 0x56ccf2 : ratio > 0.25 ? 0xf2c94c : 0xeb5757;
        const colorHi = ratio > 0.5 ? 0x9ef0ff : ratio > 0.25 ? 0xffe080 : 0xff9090;

        this.timerBar.clear();
        if (filledW > 0) {
            // ombre intérieure
            this.timerBar.fillStyle(0x000000, 0.3);
            this.timerBar.fillRoundedRect(barX - fullW / 2 + 1, barY - barH / 2 + 2, filledW - 1, barH - 2, r);
            // barre principale
            this.timerBar.fillStyle(color, 1);
            this.timerBar.fillRoundedRect(barX - fullW / 2, barY - barH / 2, filledW, barH, r);
            // reflet haut
            this.timerBar.fillStyle(0xffffff, 0.18);
            this.timerBar.fillRoundedRect(barX - fullW / 2 + 2, barY - barH / 2 + 2, filledW - 4, barH * 0.38, r * 0.5);
        }

        // Lueur pulsante sur le bord droit
        this.timerBarGlow.clear();
        if (filledW > 4) {
            this.timerBarGlow.fillStyle(colorHi, 0.45);
            this.timerBarGlow.fillCircle(barX - fullW / 2 + filledW, barY, barH * 0.7);
        }
        this.questionText.setText(this.currentQuestion.q);

        for (let i = 0; i < 4; i += 1) {
            this.choiceTexts[i].setText('');
        }
        this._refreshButtonLabels();

        if (this.p1Buttons) {
            const locked = this.leftAnswer !== null;
            this.p1Buttons.forEach(({ gfx, lbl, zone, cx, cy, fillNorm, fillHover, stroke }) => {
                gfx.setAlpha(locked ? 0.35 : 1);
                lbl.setAlpha(locked ? 0.4 : 1);
            });
        }
        if (this.p2Buttons) {
            const locked = this.rightAnswer !== null;
            this.p2Buttons.forEach(({ gfx, lbl, zone, cx, cy, fillNorm, fillHover, stroke }) => {
                gfx.setAlpha(locked ? 0.35 : 1);
                lbl.setAlpha(locked ? 0.4 : 1);
            });
        }
    }

    resolveRound() {
        if (this.roundLocked) {
            return;
        }
        this.roundLocked = true;

        const leftCorrect = this.leftAnswer === this.currentQuestion.a;
        const rightCorrect = this.rightAnswer === this.currentQuestion.a;
        const feedbackParts = [];

        if (this.leftAnswer === null) {
            feedbackParts.push('Gauche: temps ecoule');
        } else if (leftCorrect) {
            this.rightHp = Math.max(0, this.rightHp - 1);
            this.flashFighter(this.rightFighter);
            feedbackParts.push('Gauche: bonne reponse, 1 degat');
        } else {
            feedbackParts.push('Gauche: mauvaise reponse');
        }

        if (this.rightAnswer === null) {
            feedbackParts.push('Droite: temps ecoule');
        } else if (rightCorrect) {
            this.leftHp = Math.max(0, this.leftHp - 1);
            this.flashFighter(this.leftFighter);
            feedbackParts.push('Droite: bonne reponse, 1 degat');
        } else {
            feedbackParts.push('Droite: mauvaise reponse');
        }

        this.feedbackText.setText(feedbackParts.join(' | '));

        if (this.leftHp <= 0 || this.rightHp <= 0) {
            this.endGame();
            return;
        }

        this.time.delayedCall(900, () => {
            this.startRound();
        });
    }

    flashFighter(target) {
        this.tweens.add({
            targets: target,
            alpha: 0.2,
            duration: 120,
            yoyo: true,
            repeat: 2
        });
    }

    _checkBothAnswered() {
        this.updateUi();
        if (this.leftAnswer !== null && this.rightAnswer !== null) {
            this.resolveRound();
        }
    }

    _drawHearts(gfx, current, max, size, rtl) {
        gfx.clear();
        const gap = size * 0.3;
        for (let i = 0; i < max; i++) {
            const filled = i < current;
            const xi = rtl
                ? -((max - 1 - i) * (size + gap) + size)
                : i * (size + gap);
            this._drawHeart(gfx, xi, 0, size, filled);
        }
    }

    _drawHeart(gfx, x, y, s, filled) {
        const cx = x + s / 2;
        const cy = y + s / 2;
        const r  = s * 0.26;
        gfx.fillStyle(filled ? 0xff4757 : 0x222244, 1);
        gfx.fillCircle(cx - r * 0.92, cy - r * 0.5, r);
        gfx.fillCircle(cx + r * 0.92, cy - r * 0.5, r);
        const hw = s * 0.52;
        const hh = s * 0.50;
        gfx.fillTriangle(
            cx - hw, cy - r * 0.1,
            cx + hw, cy - r * 0.1,
            cx,      cy + hh
        );
        if (filled) {
            gfx.fillStyle(0xff8fa3, 0.5);
            gfx.fillCircle(cx - r * 0.5, cy - r * 0.7, r * 0.45);
        }
        gfx.lineStyle(1, filled ? 0xc0392b : 0x444466, 1);
        gfx.strokeCircle(cx - r * 0.92, cy - r * 0.5, r);
        gfx.strokeCircle(cx + r * 0.92, cy - r * 0.5, r);
    }

    _drawBtn(gfx, x, y, w, h, fill, stroke, alpha) {
        const r = Math.round(Math.min(w, h) * 0.18);
        gfx.clear();
        gfx.fillStyle(0x000000, 0.35);
        gfx.fillRoundedRect(x - w / 2 + 3, y - h / 2 + 4, w, h, r);
        gfx.fillStyle(fill, alpha);
        gfx.fillRoundedRect(x - w / 2, y - h / 2, w, h, r);
        gfx.fillStyle(0xffffff, 0.10);
        gfx.fillRoundedRect(x - w / 2 + 2, y - h / 2 + 2, w - 4, h * 0.4, r * 0.6);
        gfx.lineStyle(2.5, stroke, 1);
        gfx.strokeRoundedRect(x - w / 2, y - h / 2, w, h, r);
    }

    _createAnswerButtons(width, height) {
        if (this.p1Buttons) {
            [...this.p1Buttons, ...this.p2Buttons].forEach(({ gfx, lbl, zone }) => {
                gfx.destroy();
                lbl.destroy();
                zone.destroy();
            });
        }

        const isMobile = width < 900 || height > width;
        const pad = 10;
        const cols = 2;
        const sideW = width * (isMobile ? 0.46 : 0.22);
        const btnW = (sideW - pad * (cols + 1)) / cols;
        const btnH = Math.min(isMobile ? 72 : 68, height * (isMobile ? 0.09 : 0.1));
        const gridH = 2 * btnH + 3 * pad;
        const gridTop = height - gridH - pad;
        const baseFontPx = Math.round(btnH * (this.subject === 'fr' ? 0.33 : 0.44));
        const minFontPx = Math.max(12, Math.round(btnH * 0.22));
        const fontSize = `bold ${baseFontPx}px Arial`;

        this._answerBtnTextBasePx = baseFontPx;
        this._answerBtnTextMinPx = minFontPx;
        this._answerBtnTextMaxWidth = btnW - 16;

        this.p1Buttons = [];
        this.p2Buttons = [];

        const makeBtn = (cx, cy, fillNorm, fillHover, stroke, onPress) => {
            const gfx = this.add.graphics();
            this._drawBtn(gfx, cx, cy, btnW, btnH, fillNorm, stroke, 1);

            const zone = this.add.zone(cx, cy, btnW, btnH).setInteractive({ useHandCursor: true });
            zone.on('pointerover',  () => { this._drawBtn(gfx, cx, cy, btnW, btnH, fillHover, stroke, 1); });
            zone.on('pointerout',   () => { this._drawBtn(gfx, cx, cy, btnW, btnH, fillNorm,  stroke, 1); });
            zone.on('pointerdown',  () => {
                this._drawBtn(gfx, cx, cy, btnW, btnH, fillHover, stroke, 1);
                this.tweens.add({ targets: [gfx, lbl], scaleX: 0.93, scaleY: 0.93, duration: 70, yoyo: true });
                onPress();
            });

            const lbl = this.add.text(cx, cy, '', { font: fontSize, fill: '#ffffff' }).setOrigin(0.5).setDepth(1);
            return { gfx, lbl, zone, cx, cy, fillNorm, fillHover, stroke };
        };

        for (let i = 0; i < 4; i += 1) {
            const col = i % cols;
            const row = Math.floor(i / cols);
            const cy = gridTop + row * (btnH + pad) + btnH / 2;

            const cx1 = pad + col * (btnW + pad) + btnW / 2;
            const btn1 = makeBtn(cx1, cy, 0x1a3d7a, 0x2a5db0, 0x56ccf2, () => {
                if (!this.roundLocked && this.leftAnswer === null) {
                    this.leftAnswer = i;
                    this._checkBothAnswered();
                }
            });
            this.p1Buttons.push(btn1);

            const cx2 = width - pad - (cols - 1 - col) * (btnW + pad) - btnW / 2;
            const btn2 = makeBtn(cx2, cy, 0x7a1a1a, 0xb02a2a, 0xff7675, () => {
                if (!this.roundLocked && this.rightAnswer === null) {
                    this.rightAnswer = i;
                    this._checkBothAnswered();
                }
            });
            this.p2Buttons.push(btn2);
        }

        // Mettre à jour les labels avec les choix courants
        this._refreshButtonLabels();
    }

    _refreshButtonLabels() {
        if (!this.p1Buttons || !this.currentQuestion) { return; }
        for (let i = 0; i < 4; i++) {
            const txt = this.currentQuestion.choices[i];
            this._fitAnswerLabel(this.p1Buttons[i].lbl, txt);
            this._fitAnswerLabel(this.p2Buttons[i].lbl, txt);
        }
    }

    _fitAnswerLabel(label, text) {
        let size = this._answerBtnTextBasePx || 22;
        const minSize = this._answerBtnTextMinPx || 12;
        const maxWidth = this._answerBtnTextMaxWidth || 140;

        label.setStyle({ font: `bold ${size}px Arial` });
        label.setText(text);

        while (label.width > maxWidth && size > minSize) {
            size -= 1;
            label.setStyle({ font: `bold ${size}px Arial` });
            label.setText(text);
        }
    }

    _resetButtonStates() {
        if (!this.p1Buttons) { return; }
        [...this.p1Buttons, ...this.p2Buttons].forEach(({ gfx, lbl }) => {
            gfx.setAlpha(1);
            lbl.setAlpha(1);
        });
        this._refreshButtonLabels();
    }



    endGame() {
        this.roundLocked = true;
        this.timerEvent.remove(false);
        let winner = 'Egalite';
        if (this.leftHp > this.rightHp) {
            winner = 'Joueur gauche';
        }
        if (this.rightHp > this.leftHp) {
            winner = 'Joueur droite';
        }

        this.questionText.setText(`Victoire: ${winner}`);
        this.choiceTexts.forEach((choiceText) => choiceText.setText(''));
        this.turnText.setText('Partie terminee');
        this.feedbackText.setText('Appuyez sur ESC ou utilisez le bouton RETOUR');
    }
}
