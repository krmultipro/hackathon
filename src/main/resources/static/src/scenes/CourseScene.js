export default class CourseScene extends Phaser.Scene {
    constructor() {
        super({ key: 'CourseScene' });
    }

    create() {
        const { width, height } = this.scale;

        this.bg = this.add.rectangle(0, 0, 1, 1, 0x101626).setOrigin(0);

        this.titleText = this.add.text(0, 0, 'Choisir une matiere', {
            font: 'bold 42px Arial',
            fill: '#ffffff'
        }).setOrigin(0.5);

        this.mathButton = this.add.text(0, 0, '[ MATHEMATIQUES ]', {
            font: '30px Arial',
            fill: '#56ccf2'
        }).setOrigin(0.5).setInteractive({ useHandCursor: true });

        this.frenchButton = this.add.text(0, 0, '[ FRANCAIS ]', {
            font: '30px Arial',
            fill: '#f2c94c'
        }).setOrigin(0.5).setInteractive({ useHandCursor: true });

        this.backText = this.add.text(0, 0, '[ RETOUR ]', {
            font: '18px Arial',
            fill: '#aaaaaa'
        }).setOrigin(0.5).setInteractive({ useHandCursor: true });

        this.mathButton.on('pointerover', () => this.mathButton.setStyle({ fill: '#ffffff' }));
        this.mathButton.on('pointerout', () => this.mathButton.setStyle({ fill: '#56ccf2' }));
        this.mathButton.on('pointerdown', () => this.scene.start('MathCourseScene'));

        this.frenchButton.on('pointerover', () => this.frenchButton.setStyle({ fill: '#ffffff' }));
        this.frenchButton.on('pointerout', () => this.frenchButton.setStyle({ fill: '#f2c94c' }));
        this.frenchButton.on('pointerdown', () => this.scene.start('FrenchCourseScene'));

        this.backText.on('pointerover', () => this.backText.setStyle({ fill: '#ffffff' }));
        this.backText.on('pointerout', () => this.backText.setStyle({ fill: '#aaaaaa' }));
        this.backText.on('pointerdown', () => this.scene.start('MenuScene'));

        this.input.keyboard.on('keydown-ESC', () => this.scene.start('MenuScene'));

        this._layout(width, height);

        this.scale.on('resize', this._onResize, this);
        this.events.once('shutdown', () => {
            this.scale.off('resize', this._onResize, this);
        });
    }

    _onResize() {
        if (this._resizeTimer) { this._resizeTimer.remove(); }
        this._resizeTimer = this.time.delayedCall(150, () => {
            const width = window.innerWidth;
            const height = window.innerHeight;
            this.scale.resize(width, height);
            this._layout(width, height);
        });
    }

    _layout(width, height) {
        const base = Math.min(width, height);
        const titleSize = Phaser.Math.Clamp(Math.round(base * 0.08), 26, 54);
        const btnSize = Phaser.Math.Clamp(Math.round(base * 0.05), 20, 40);
        const backSize = Phaser.Math.Clamp(Math.round(base * 0.03), 14, 24);

        this.bg.setSize(width, height);
        this.titleText.setPosition(width / 2, height * 0.28).setStyle({ font: `bold ${titleSize}px Arial` });
        this.mathButton.setPosition(width / 2, height * 0.48).setStyle({ font: `${btnSize}px Arial` });
        this.frenchButton.setPosition(width / 2, height * 0.60).setStyle({ font: `${btnSize}px Arial` });
        this.backText.setPosition(width / 2, height * 0.75).setStyle({ font: `${backSize}px Arial` });
    }
}
