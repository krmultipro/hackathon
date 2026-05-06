import { loadQuestions } from '../modules/course/courseService.js';
import {
    createMatchState,
    getFeedbackText,
    getPlayerStateText,
    getWinnerLabel,
    isGameOver,
    resolveRound,
    shouldResolveRound,
    startRound,
    submitAnswer,
    tick
} from '../modules/match/matchLogic.js';
import { MATCH_DEFAULTS } from '../utils/constants.js';

export default class GameScene extends Phaser.Scene {
    constructor() {
        super({ key: 'GameScene' });
        this.maxHp = MATCH_DEFAULTS.MAX_HP;
        this.timePerQuestion = MATCH_DEFAULTS.TIME_PER_QUESTION;
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

        this.questions = loadQuestions(this.subject);
        this.matchState = createMatchState({
            maxHp: this.maxHp,
            timePerQuestion: this.timePerQuestion,
            questions: this.questions
        });

        this.bg = this.add.rectangle(0, 0, 1, 1, 0x101626).setOrigin(0);
        this.mobileDivider = this.add.rectangle(0, 0, 1, 1, 0x334766).setAlpha(0.45);

        const p1tex = this.p1char === 'boy' ? 'p1boy' : 'p1girl';
        const p2tex = this.p2char === 'boy' ? 'p2boy' : 'p2girl';

        this.leftFighter = this.add.image(0, 0, p1tex);
        this.rightFighter = this.add.image(0, 0, p2tex).setFlipX(true);

        this.turnText = this.add.text(0, 0, 'Mode simultane', {
            font: '24px Arial',
            fill: '#f2c94c'
        }).setOrigin(0.5);

        this.timerBarBg = this.add.graphics();
        this.timerBar = this.add.graphics();
        this.timerBarGlow = this.add.graphics().setBlendMode(Phaser.BlendModes.ADD);

        this.leftHpGfx = this.add.graphics();
        this.rightHpGfx = this.add.graphics();

        this.sceneBg = this.add.image(0, 0, 'sceneBg').setAlpha(0.2).setDepth(0);

        this.questionText = this.add.text(0, 0, '', {
            font: '26px Arial',
            fill: '#ffffff',
            align: 'center',
            wordWrap: { width: 400 }
        }).setOrigin(0.5, 0);
        this.questionTextBottom = this.add.text(0, 0, '', {
            font: '26px Arial',
            fill: '#ffffff',
            align: 'center',
            wordWrap: { width: 400 }
        }).setOrigin(0.5, 0);
        this.winTextTop = this.add.text(0, 0, '', {
            font: '24px Arial',
            fill: '#56ccf2',
            align: 'center',
            wordWrap: { width: 400 }
        }).setOrigin(0.5);
        this.winTextBottom = this.add.text(0, 0, '', {
            font: '24px Arial',
            fill: '#56ccf2',
            align: 'center',
            wordWrap: { width: 400 }
        }).setOrigin(0.5);

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

        this.backButton = this.add.text(0, 0, 'Retour', {
            font: 'bold 18px Arial',
            fill: '#ffffff',
            backgroundColor: '#1f4ed8',
            padding: { x: 18, y: 10 }
        }).setOrigin(0, 0.5).setInteractive({ useHandCursor: true });

        this.backButton.on('pointerover', () => this.backButton.setStyle({ backgroundColor: '#2563eb' }));
        this.backButton.on('pointerout', () => this.backButton.setStyle({ backgroundColor: '#1f4ed8' }));
        this.backButton.on('pointerdown', () => this.goBack());

        this.keys = this.input.keyboard.addKeys({
            esc: Phaser.Input.Keyboard.KeyCodes.ESC,
            leftA: Phaser.Input.Keyboard.KeyCodes.A,
            leftB: Phaser.Input.Keyboard.KeyCodes.Z,
            leftC: Phaser.Input.Keyboard.KeyCodes.E,
            leftD: Phaser.Input.Keyboard.KeyCodes.R,
            rightA: Phaser.Input.Keyboard.KeyCodes.U,
            rightB: Phaser.Input.Keyboard.KeyCodes.I,
            rightC: Phaser.Input.Keyboard.KeyCodes.O,
            rightD: Phaser.Input.Keyboard.KeyCodes.P
        });

        this.input.keyboard.on('keydown-ESC', () => this.goBack());
        this._registerDesktopAnswerShortcuts();
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

    goBack() {
        this.scene.start(this.previousScene, {
            subject: this.subject,
            previousScene: this.characterSelectPreviousScene
        });
    }

    _onResize() {
        if (this._resizeTimer) {
            this._resizeTimer.remove();
        }

        this._resizeTimer = this.time.delayedCall(150, () => {
            const width = window.innerWidth;
            const height = window.innerHeight;
            this.scale.resize(width, height);
            this._createAnswerButtons(width, height);
            this._layout(width, height);
            this.updateUi();
        });
    }

    _layout(width, height) {
        const isMobile = this._isMobileLayout(width, height);
        const base = Math.min(width, height);
        const fs = (n, min, max) => `${Phaser.Math.Clamp(Math.round(n * base / 600), min, max)}px Arial`;

        this.bg.setSize(width, height);
        this.mobileDivider.setVisible(isMobile);
        if (isMobile) {
            this.mobileDivider.setPosition(width / 2, height / 2).setSize(width, 4);
        }

        const hSize = Phaser.Math.Clamp(Math.round(base * 0.033), 12, 22);
        this._heartSize = hSize;
        const heartGap = hSize * 0.3;
        const heartGroupW = this.matchState.maxHp * hSize + (this.matchState.maxHp - 1) * heartGap;
        this.leftHpGfx.setPosition(
            isMobile ? 12 + heartGroupW : 12,
            isMobile ? 10 + hSize : 10
        );
        this.rightHpGfx.setPosition(width - 12, isMobile ? height - hSize - 10 : 10);
        this.leftHpGfx.setAngle(isMobile ? 180 : 0);
        this.rightHpGfx.setAngle(0);
        this._drawHearts(this.leftHpGfx, this.matchState.leftHp, this.matchState.maxHp, hSize, isMobile);
        this._drawHearts(this.rightHpGfx, this.matchState.rightHp, this.matchState.maxHp, hSize, true);

        const barW = width * (isMobile ? 0.62 : 0.5);
        const barH = Phaser.Math.Clamp(Math.round(height * 0.028), 12, 22);
        const barY = isMobile ? height * 0.5 : height * 0.165;
        const turnY = isMobile ? Math.round(barY - barH - 24) : Math.round((10 + hSize + barY) / 2);
        this.turnText.setPosition(width / 2, turnY).setStyle({ font: fs(24, 14, 30) });
        this._timerBarW = barW;
        this._timerBarH = barH;
        this._timerBarY = barY;
        this._timerBarX = width / 2;

        const r = barH / 2;
        this.timerBarBg.clear();
        this.timerBarBg.fillStyle(0x0a0a1a, 0.7);
        this.timerBarBg.fillRoundedRect(width / 2 - barW / 2 - 2, barY - barH / 2 - 2, barW + 4, barH + 4, r + 1);
        this.timerBarBg.lineStyle(1.5, 0x444477, 1);
        this.timerBarBg.strokeRoundedRect(width / 2 - barW / 2 - 2, barY - barH / 2 - 2, barW + 4, barH + 4, r + 1);
        this.escText.setPosition(width - 12, isMobile ? height / 2 - 22 : height - 24).setStyle({ font: fs(14, 10, 18) });

        const targetCharH = Phaser.Math.Clamp(Math.round((isMobile ? 0.18 : 0.42) * height), 100, 320);
        const leftImg = this.leftFighter.texture.getSourceImage();
        const rightImg = this.rightFighter.texture.getSourceImage();
        const scaleL = targetCharH / leftImg.height;
        const scaleR = targetCharH / rightImg.height;

        const btnPad = 10;
        const btnH = Math.min(isMobile ? 58 : 68, height * (isMobile ? 0.072 : 0.1));
        const gridRows = isMobile ? 2 : 1;
        const gridH = gridRows * btnH + (gridRows + 1) * btnPad;
        const sideW = width * (isMobile ? 0.46 : 0.22);
        const fighterYTop = isMobile ? height * 0.28 : height - gridH - btnPad - targetCharH / 2 - 28;
        const fighterYBottom = isMobile ? height * 0.72 : fighterYTop;
        const fighterXLeft = isMobile ? width / 2 : sideW / 2;
        const fighterXRight = isMobile ? width / 2 : width - sideW / 2;

        this.leftFighter.setPosition(fighterXLeft, fighterYTop).setScale(scaleL).setAngle(isMobile ? 180 : 0);
        this.rightFighter.setPosition(fighterXRight, fighterYBottom).setScale(scaleR).setFlipX(!isMobile).setAngle(0);

        const blockTop = isMobile ? height * 0.45 : height * 0.28;
        const blockWidth = isMobile ? width * 0.72 : width * 0.5;

        this.sceneBg.setPosition(width / 2, height / 2);
        const bgScaleW = width / this.sceneBg.width;
        const bgScaleH = height / this.sceneBg.height;
        const bgScale = Math.max(bgScaleW, bgScaleH);
        this.sceneBg.setScale(bgScale);

        this.questionText
            .setPosition(width / 2, blockTop)
            .setStyle({ font: fs(26, 15, 32) })
            .setAngle(isMobile ? 180 : 0)
            .setWordWrapWidth(blockWidth);
        this.questionTextBottom
            .setVisible(isMobile)
            .setPosition(width / 2, height * 0.55)
            .setStyle({ font: fs(26, 15, 32) })
            .setAngle(0)
            .setWordWrapWidth(blockWidth);
        this.winTextTop
            .setVisible(isMobile)
            .setPosition(width / 2, this._timerBarY - 34)
            .setStyle({ font: fs(22, 14, 28) })
            .setAngle(180)
            .setWordWrapWidth(blockWidth);
        this.winTextBottom
            .setVisible(isMobile)
            .setPosition(width / 2, this._timerBarY + 34)
            .setStyle({ font: fs(22, 14, 28) })
            .setAngle(0)
            .setWordWrapWidth(blockWidth);

        const gap = isMobile ? 32 : 42;
        for (let i = 0; i < 4; i += 1) {
            this.choiceTexts[i]
                .setPosition(width / 2, blockTop + 62 + i * gap)
                .setStyle({ font: fs(22, 14, 28) });
        }

        this.feedbackText
            .setPosition(width / 2, blockTop + (isMobile ? 120 : 250))
            .setStyle({ font: fs(20, 12, 24) })
            .setWordWrapWidth(blockWidth);

        this.helpText
            .setPosition(width / 2, blockTop + (isMobile ? 148 : 300))
            .setStyle({ font: fs(16, 10, 20) })
            .setText(isMobile ? '' : 'PC: J1 A/Z/E/R | J2 U/I/O/P');

        this.backButton
            .setPosition(12, isMobile ? height / 2 + 26 : height - 20)
            .setStyle({
                font: `bold ${Phaser.Math.Clamp(Math.round((isMobile ? 18 : 16) * base / 600), isMobile ? 16 : 11, isMobile ? 24 : 20)}px Arial`,
                padding: { x: isMobile ? 22 : 18, y: isMobile ? 12 : 10 }
            });
    }

    update() {
        if (this.matchState.roundLocked) {
            return;
        }
    }

    onTick() {
        const result = tick(this.matchState);
        this.updateUi();

        if (result.resolved) {
            this.afterRoundResolution(result);
        }
    }

    startRound() {
        startRound(this.matchState);
        this.feedbackText.setText('');
        this._resetButtonStates();
        this.updateUi();
    }

    updateUi() {
        this.turnText.setText('');
        this._drawHearts(this.leftHpGfx, this.matchState.leftHp, this.matchState.maxHp, this._heartSize || 16, false);
        this._drawHearts(this.rightHpGfx, this.matchState.rightHp, this.matchState.maxHp, this._heartSize || 16, true);

        const ratio = this.matchState.timeLeft / this.matchState.timePerQuestion;
        const fullW = this._timerBarW || 200;
        const barH = this._timerBarH || 16;
        const barY = this._timerBarY || 100;
        const barX = this._timerBarX || (this.scale.width / 2);
        const filledW = Math.max(0, fullW * ratio);
        const r = barH / 2;
        const color = ratio > 0.5 ? 0x56ccf2 : ratio > 0.25 ? 0xf2c94c : 0xeb5757;
        const colorHi = ratio > 0.5 ? 0x9ef0ff : ratio > 0.25 ? 0xffe080 : 0xff9090;

        this.timerBar.clear();
        if (filledW > 0) {
            this.timerBar.fillStyle(0x000000, 0.3);
            this.timerBar.fillRoundedRect(barX - fullW / 2 + 1, barY - barH / 2 + 2, filledW - 1, barH - 2, r);
            this.timerBar.fillStyle(color, 1);
            this.timerBar.fillRoundedRect(barX - fullW / 2, barY - barH / 2, filledW, barH, r);
            this.timerBar.fillStyle(0xffffff, 0.18);
            this.timerBar.fillRoundedRect(barX - fullW / 2 + 2, barY - barH / 2 + 2, filledW - 4, barH * 0.38, r * 0.5);
        }

        this.timerBarGlow.clear();
        if (filledW > 4) {
            this.timerBarGlow.fillStyle(colorHi, 0.45);
            this.timerBarGlow.fillCircle(barX - fullW / 2 + filledW, barY, barH * 0.7);
        }

        this.questionText.setText(this.matchState.currentQuestion ? this.matchState.currentQuestion.q : '');
        this.questionTextBottom.setText(this.matchState.currentQuestion ? this.matchState.currentQuestion.q : '');
        this.winTextTop.setText('');
        this.winTextBottom.setText('');

        for (let i = 0; i < 4; i += 1) {
            this.choiceTexts[i].setText('');
        }
        this._refreshButtonLabels();

        if (this.p1Buttons) {
            const locked = this.matchState.leftAnswer !== null;
            this.p1Buttons.forEach(({ gfx, lbl }) => {
                gfx.setAlpha(locked ? 0.35 : 1);
                lbl.setAlpha(locked ? 0.4 : 1);
            });
        }

        if (this.p2Buttons) {
            const locked = this.matchState.rightAnswer !== null;
            this.p2Buttons.forEach(({ gfx, lbl }) => {
                gfx.setAlpha(locked ? 0.35 : 1);
                lbl.setAlpha(locked ? 0.4 : 1);
            });
        }
    }

    afterRoundResolution(result) {
        if (result.rightTookDamage) {
            this.flashFighter(this.rightFighter);
        }

        if (result.leftTookDamage) {
            this.flashFighter(this.leftFighter);
        }

        this.feedbackText.setText(result.feedbackText);
        this.updateUi();

        if (result.gameOver) {
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
        if (shouldResolveRound(this.matchState)) {
            const result = resolveRound(this.matchState);
            this.afterRoundResolution(result);
        }
    }

    _registerDesktopAnswerShortcuts() {
        const bindings = [
            { key: 'keydown-A', side: 'left', answerIndex: 0 },
            { key: 'keydown-Z', side: 'left', answerIndex: 1 },
            { key: 'keydown-E', side: 'left', answerIndex: 2 },
            { key: 'keydown-R', side: 'left', answerIndex: 3 },
            { key: 'keydown-U', side: 'right', answerIndex: 0 },
            { key: 'keydown-I', side: 'right', answerIndex: 1 },
            { key: 'keydown-O', side: 'right', answerIndex: 2 },
            { key: 'keydown-P', side: 'right', answerIndex: 3 }
        ];

        bindings.forEach(({ key, side, answerIndex }) => {
            this.input.keyboard.on(key, () => {
                if (this._isMobileLayout()) {
                    return;
                }

                const changed = submitAnswer(this.matchState, side, answerIndex);
                if (changed) {
                    this._checkBothAnswered();
                }
            });
        });
    }

    _isMobileLayout(width = this.scale.width, height = this.scale.height) {
        return width < 900 || height > width;
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
        const r = s * 0.26;
        gfx.fillStyle(filled ? 0xff4757 : 0x222244, 1);
        gfx.fillCircle(cx - r * 0.92, cy - r * 0.5, r);
        gfx.fillCircle(cx + r * 0.92, cy - r * 0.5, r);
        const hw = s * 0.52;
        const hh = s * 0.50;
        gfx.fillTriangle(
            cx - hw, cy - r * 0.1,
            cx + hw, cy - r * 0.1,
            cx, cy + hh
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
            [...this.p1Buttons, ...this.p2Buttons].forEach(({ gfx, lbl, keyLbl, zone }) => {
                gfx.destroy();
                lbl.destroy();
                if (keyLbl) {
                    keyLbl.destroy();
                }
                zone.destroy();
            });
        }

        const isMobile = this._isMobileLayout(width, height);
        const pad = 10;
        const cols = isMobile ? 2 : 4;
        const rows = isMobile ? 2 : 1;
        const sideW = width * (isMobile ? 0.56 : 0.22);
        const btnW = (sideW - pad * (cols + 1)) / cols;
        const btnH = Math.min(isMobile ? 58 : 68, height * (isMobile ? 0.072 : 0.1));
        const gridH = rows * btnH + (rows + 1) * pad;
        const topGridTop = isMobile ? height * 0.05 : height - gridH - pad;
        const bottomGridTop = isMobile ? height - gridH - height * 0.05 : height - gridH - pad;
        const leftKeys = ['A', 'Z', 'E', 'R'];
        const rightKeys = ['U', 'I', 'O', 'P'];
        const baseFontPx = Math.round(btnH * (this.subject === 'fr' ? 0.33 : 0.44));
        const minFontPx = Math.max(12, Math.round(btnH * 0.22));
        const fontSize = `bold ${baseFontPx}px Arial`;

        this._answerBtnTextBasePx = baseFontPx;
        this._answerBtnTextMinPx = minFontPx;
        this._answerBtnTextMaxWidth = btnW - 28;

        this.p1Buttons = [];
        this.p2Buttons = [];

        const makeBtn = (cx, cy, fillNorm, fillHover, stroke, keyText, onPress) => {
            const gfx = this.add.graphics();
            this._drawBtn(gfx, cx, cy, btnW, btnH, fillNorm, stroke, 1);

            const zone = this.add.zone(cx, cy, btnW, btnH).setInteractive({ useHandCursor: true });
            zone.on('pointerover', () => {
                this._drawBtn(gfx, cx, cy, btnW, btnH, fillHover, stroke, 1);
            });
            zone.on('pointerout', () => {
                this._drawBtn(gfx, cx, cy, btnW, btnH, fillNorm, stroke, 1);
            });
            zone.on('pointerdown', () => {
                this._drawBtn(gfx, cx, cy, btnW, btnH, fillHover, stroke, 1);
                this.tweens.add({ targets: [gfx, lbl], scaleX: 0.93, scaleY: 0.93, duration: 70, yoyo: true });
                onPress();
            });

            const lbl = this.add.text(cx, cy, '', {
                font: fontSize,
                fill: '#ffffff',
                align: 'center'
            }).setOrigin(0.5).setDepth(1);

            let keyLbl = null;
            if (!isMobile) {
                keyLbl = this.add.text(cx, cy - btnH / 2 - 12, keyText, {
                    font: 'bold 14px Arial',
                    fill: '#f2c94c',
                    backgroundColor: '#0f172a',
                    padding: { x: 8, y: 4 }
                }).setOrigin(0.5).setDepth(1);
            }

            return { gfx, lbl, keyLbl, zone };
        };

        for (let i = 0; i < 4; i += 1) {
            const col = isMobile ? i % cols : i;
            const row = isMobile ? Math.floor(i / cols) : 0;
            const cy1 = topGridTop + row * (btnH + pad) + btnH / 2;
            const cy2 = bottomGridTop + row * (btnH + pad) + btnH / 2;

            const cx1 = isMobile
                ? width / 2 - sideW / 2 + pad + col * (btnW + pad) + btnW / 2
                : pad + col * (btnW + pad) + btnW / 2;
            const btn1 = makeBtn(cx1, cy1, 0x1a3d7a, 0x2a5db0, 0x56ccf2, leftKeys[i], () => {
                const changed = submitAnswer(this.matchState, 'left', i);
                if (changed) {
                    this._checkBothAnswered();
                }
            });
            if (isMobile) {
                btn1.lbl.setAngle(180);
            }
            this.p1Buttons.push(btn1);

            const cx2 = isMobile
                ? width / 2 - sideW / 2 + pad + col * (btnW + pad) + btnW / 2
                : width - pad - (cols - 1 - col) * (btnW + pad) - btnW / 2;
            const btn2 = makeBtn(cx2, cy2, 0x7a1a1a, 0xb02a2a, 0xff7675, rightKeys[i], () => {
                const changed = submitAnswer(this.matchState, 'right', i);
                if (changed) {
                    this._checkBothAnswered();
                }
            });
            this.p2Buttons.push(btn2);
        }

        this._refreshButtonLabels();
    }

    _refreshButtonLabels() {
        if (!this.p1Buttons || !this.matchState.currentQuestion) {
            return;
        }

        const isMobile = this._isMobileLayout();
        const leftKeys = ['A', 'Z', 'E', 'R'];
        const rightKeys = ['U', 'I', 'O', 'P'];

        for (let i = 0; i < 4; i += 1) {
            const txt = this.matchState.currentQuestion.choices[i];
            const leftLabel = txt;
            const rightLabel = txt;
            this._fitAnswerLabel(this.p1Buttons[i].lbl, leftLabel);
            this._fitAnswerLabel(this.p2Buttons[i].lbl, rightLabel);

            if (!isMobile) {
                this.p1Buttons[i].keyLbl.setText(leftKeys[i]);
                this.p2Buttons[i].keyLbl.setText(rightKeys[i]);
            }
        }
    }

    _fitAnswerLabel(label, text) {
        let size = this._answerBtnTextBasePx || 22;
        const minSize = this._answerBtnTextMinPx || 12;
        const maxWidth = this._answerBtnTextMaxWidth || 140;

        label.setStyle({ font: `bold ${size}px Arial` });
        label.setText(text);
        label.setWordWrapWidth(maxWidth);

        while (label.width > maxWidth && size > minSize) {
            size -= 1;
            label.setStyle({ font: `bold ${size}px Arial` });
            label.setText(text);
            label.setWordWrapWidth(maxWidth);
        }
    }

    _resetButtonStates() {
        if (!this.p1Buttons) {
            return;
        }

        [...this.p1Buttons, ...this.p2Buttons].forEach(({ gfx, lbl }) => {
            gfx.setAlpha(1);
            lbl.setAlpha(1);
        });
        this._refreshButtonLabels();
    }

    endGame() {
        this.matchState.roundLocked = true;
        this.timerEvent.remove(false);
        const winner = getWinnerLabel(this.matchState);
        this.choiceTexts.forEach((choiceText) => choiceText.setText(''));
        this.feedbackText.setText('');

        if (this._isMobileLayout()) {
            this.turnText.setText('');

            this.questionText
                .setText('')
                .setAngle(180)
                .setPosition(this.scale.width / 2, this._timerBarY - 66)
                .setOrigin(0.5, 0.5);

            this.questionTextBottom
                .setText('')
                .setAngle(0)
                .setPosition(this.scale.width / 2, this._timerBarY + 42)
                .setOrigin(0.5, 0.5);

            this.winTextTop
                .setText(winner === 'Joueur bleu' ? 'Vous avez gagne' : winner === 'Match nul' ? 'Match nul' : '')
                .setStyle({ fill: '#ffffff' });
            this.winTextBottom
                .setText(winner === 'Joueur rouge' ? 'Vous avez gagne' : winner === 'Match nul' ? 'Match nul' : '')
                .setStyle({ fill: '#ffffff' });
        } else {
            this.turnText.setText('Partie terminee').setStyle({ fill: '#f2c94c' });
            this.questionText.setText(`Victoire: ${winner}`).setAngle(0);
            this.questionTextBottom.setText('');
            this.winTextTop.setText('');
            this.winTextBottom.setText('');
        }
    }
}
