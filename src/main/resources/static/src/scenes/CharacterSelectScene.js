export default class CharacterSelectScene extends Phaser.Scene {
    constructor() {
        super({ key: 'CharacterSelectScene' });
    }

    init(data) {
        this.subject = (data && data.subject) ? data.subject : 'math';
        this.previousScene = (data && data.previousScene) ? data.previousScene : 'MenuScene';
    }

    create() {
        const { width, height } = this.scale;

        this.p1choice = null;
        this.p2choice = null;

        this.bg = this.add.rectangle(0, 0, 1, 1, 0x101626).setOrigin(0);
        this.separator = this.add.rectangle(0, 0, 4, 10, 0x333355);

        this.p1Title = this.add.text(0, 0, 'Joueur 1', {
            font: 'bold 30px Arial',
            fill: '#56ccf2'
        }).setOrigin(0.5);

        this.p2Title = this.add.text(0, 0, 'Joueur 2', {
            font: 'bold 30px Arial',
            fill: '#ff7675'
        }).setOrigin(0.5);

        this.p1BoyImg = this.add.image(0, 0, 'p1boy').setInteractive({ useHandCursor: true });
        this.p1GirlImg = this.add.image(0, 0, 'p1girl').setInteractive({ useHandCursor: true });
        this.p2BoyImg = this.add.image(0, 0, 'p2boy').setInteractive({ useHandCursor: true });
        this.p2GirlImg = this.add.image(0, 0, 'p2girl').setInteractive({ useHandCursor: true });

        this.p1BoyImg.on('pointerdown',  () => { if (!this.p1choice) this._selectP1('boy'); });
        this.p1GirlImg.on('pointerdown', () => { if (!this.p1choice) this._selectP1('girl'); });
        this.p2BoyImg.on('pointerdown',  () => { if (!this.p2choice) this._selectP2('boy'); });
        this.p2GirlImg.on('pointerdown', () => { if (!this.p2choice) this._selectP2('girl'); });

        this.p1StatusText = this.add.text(0, 0, 'Cliquez/Touchez pour selectionner votre personnage', {
            font: '20px Arial', fill: '#f2c94c'
        }).setOrigin(0.5);

        this.p2StatusText = this.add.text(0, 0, 'Cliquez/Touchez pour selectionner votre personnage', {
            font: '20px Arial', fill: '#f2c94c'
        }).setOrigin(0.5);

        this.p1Frame = this.add.rectangle(0, 0, 10, 10, 0x2d9cdb).setAlpha(0).setDepth(-1);
        this.p2Frame = this.add.rectangle(0, 0, 10, 10, 0xeb5757).setAlpha(0).setDepth(-1);

        this.backButton = this.add.text(0, 0, 'Retour', {
            font: 'bold 18px Arial',
            fill: '#ffffff',
            backgroundColor: '#1f4ed8',
            padding: { x: 18, y: 10 }
        }).setOrigin(0, 0.5).setInteractive({ useHandCursor: true });

        this.backButton.on('pointerover', () => this.backButton.setStyle({ backgroundColor: '#2563eb' }));
        this.backButton.on('pointerout', () => this.backButton.setStyle({ backgroundColor: '#1f4ed8' }));
        this.backButton.on('pointerdown', () => this.scene.start(this.previousScene));

        this.input.keyboard.on('keydown-ESC', () => this.scene.start(this.previousScene));

        this._layout(width, height);
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
            this._layout(width, height);
        });
    }

    _layout(width, height) {
        const isMobile = width < 900 || height > width;
        const base = Math.min(width, height);
        const titleSize = Phaser.Math.Clamp(Math.round(base * 0.06), 22, 42);
        const headingSize = Phaser.Math.Clamp(Math.round(base * 0.05), 20, 34);
        const hintSize = Phaser.Math.Clamp(Math.round(base * 0.032), 14, 24);

        this.bg.setSize(width, height);

        if (isMobile) {
            this.separator.setPosition(width / 2, height / 2).setSize(width, 4);
        } else {
            this.separator.setPosition(width / 2, height / 2).setSize(4, height);
        }

        const p1Area = isMobile
            ? { cx: width / 2, top: height * 0.14, bottom: height * 0.50 }
            : { cx: width / 4, top: height * 0.14, bottom: height * 0.92 };
        const p2Area = isMobile
            ? { cx: width / 2, top: height * 0.56, bottom: height * 0.94 }
            : { cx: (3 * width) / 4, top: height * 0.14, bottom: height * 0.92 };

        const p1Gap = isMobile ? width * 0.2 : width * 0.08;
        const p2Gap = isMobile ? width * 0.2 : width * 0.08;
        this._boyP1x = p1Area.cx - p1Gap;
        this._girlP1x = p1Area.cx + p1Gap;
        this._boyP2x = p2Area.cx - p2Gap;
        this._girlP2x = p2Area.cx + p2Gap;

        const p1CharY = (p1Area.top + p1Area.bottom) / 2 - (isMobile ? 6 : 18);
        const p2CharY = (p2Area.top + p2Area.bottom) / 2 - (isMobile ? 6 : 18);

        const srcP1Boy  = this.textures.get('p1boy').getSourceImage();
        const srcP1Girl = this.textures.get('p1girl').getSourceImage();
        const srcP2Boy  = this.textures.get('p2boy').getSourceImage();
        const srcP2Girl = this.textures.get('p2girl').getSourceImage();
        const targetCharH = Phaser.Math.Clamp(Math.round((isMobile ? 0.22 : 0.30) * height), 110, 270);
        const scaleP1Boy  = targetCharH / srcP1Boy.height;
        const scaleP1Girl = targetCharH / srcP1Girl.height;
        const scaleP2Boy  = targetCharH / srcP2Boy.height;
        const scaleP2Girl = targetCharH / srcP2Girl.height;

        this.p1BoyImg.setPosition(this._boyP1x, p1CharY).setScale(scaleP1Boy).setAngle(isMobile ? 180 : 0);
        this.p1GirlImg.setPosition(this._girlP1x, p1CharY).setScale(scaleP1Girl).setAngle(isMobile ? 180 : 0);
        this.p2BoyImg.setPosition(this._boyP2x, p2CharY).setScale(scaleP2Boy).setAngle(0);
        this.p2GirlImg.setPosition(this._girlP2x, p2CharY).setScale(scaleP2Girl).setAngle(0);

        this.p1Title
            .setPosition(p1Area.cx, isMobile ? p1Area.bottom - 34 : p1Area.top)
            .setStyle({ font: `bold ${headingSize}px Arial` })
            .setAngle(isMobile ? 180 : 0);
        this.p2Title
            .setPosition(p2Area.cx, p2Area.top)
            .setStyle({ font: `bold ${headingSize}px Arial` })
            .setAngle(0);

        this.p1StatusText
            .setPosition(p1Area.cx, isMobile ? p1Area.top + 6 : p1Area.bottom - 8)
            .setStyle({ font: `${hintSize}px Arial` })
            .setAngle(isMobile ? 180 : 0);
        this.p2StatusText
            .setPosition(p2Area.cx, p2Area.bottom - 24)
            .setStyle({ font: `${hintSize}px Arial` })
            .setAngle(0);
        this.backButton
            .setPosition(12, height - (isMobile ? 30 : 20))
            .setStyle({
                font: `bold ${Math.max(isMobile ? 18 : hintSize, hintSize)}px Arial`,
                padding: { x: isMobile ? 22 : 18, y: isMobile ? 12 : 10 }
            });

        const selP1src = this.p1choice === 'girl' ? srcP1Girl : srcP1Boy;
        const selP1scale = this.p1choice === 'girl' ? scaleP1Girl : scaleP1Boy;
        const selP2src = this.p2choice === 'girl' ? srcP2Girl : srcP2Boy;
        const selP2scale = this.p2choice === 'girl' ? scaleP2Girl : scaleP2Boy;
        const frameW1 = selP1src.width * selP1scale + 18;
        const frameH1 = selP1src.height * selP1scale + 18;
        const frameW2 = selP2src.width * selP2scale + 18;
        const frameH2 = selP2src.height * selP2scale + 18;
        this.p1Frame.setSize(frameW1, frameH1).setPosition(this.p1choice === 'girl' ? this._girlP1x : this._boyP1x, p1CharY);
        this.p2Frame.setSize(frameW2, frameH2).setPosition(this.p2choice === 'girl' ? this._girlP2x : this._boyP2x, p2CharY);

        if (this.p1choice === null) {
            this.p1Frame.setAlpha(0);
        } else {
            this.p1Frame.setAlpha(0.35);
        }

        if (this.p2choice === null) {
            this.p2Frame.setAlpha(0);
        } else {
            this.p2Frame.setAlpha(0.35);
        }
    }

    _selectP1(choice) {
        this.p1choice = choice;
        const x = choice === 'boy' ? this._boyP1x : this._girlP1x;
        this.p1Frame.setPosition(x, this.p1Frame.y).setAlpha(0.35);
        this.p1StatusText
            .setText(choice === 'boy' ? 'Garçon sélectionné !' : 'Fille sélectionnée !')
            .setStyle({ fill: '#00ff88' });
        this._checkBothReady();
    }

    _selectP2(choice) {
        this.p2choice = choice;
        const x = choice === 'boy' ? this._boyP2x : this._girlP2x;
        this.p2Frame.setPosition(x, this.p2Frame.y).setAlpha(0.35);
        this.p2StatusText
            .setText(choice === 'boy' ? 'Garçon sélectionné !' : 'Fille sélectionnée !')
            .setStyle({ fill: '#00ff88' });
        this._checkBothReady();
    }

    _checkBothReady() {
        if (this.p1choice !== null && this.p2choice !== null) {
            this.time.delayedCall(700, () => {
                this.scene.start('GameScene', {
                    p1char: this.p1choice,
                    p2char: this.p2choice,
                    subject: this.subject,
                    previousScene: 'CharacterSelectScene',
                    characterSelectPreviousScene: this.previousScene
                });
            });
        }
    }
}
