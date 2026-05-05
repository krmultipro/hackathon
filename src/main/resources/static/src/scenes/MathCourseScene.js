export default class MathCourseScene extends Phaser.Scene {
    constructor() {
        super({ key: 'MathCourseScene' });
    }

    create() {
        const { width, height } = this.scale;

        this.bg = this.add.rectangle(0, 0, 1, 1, 0x101626).setOrigin(0);

        this.titleText = this.add.text(0, 0, 'Cours de mathematiques', {
            font: 'bold 40px Arial',
            fill: '#56ccf2'
        }).setOrigin(0.5);

        this.lessonText = this.add.text(0, 0,
            'Lecon rapide:\n\n' +
            '1) Priorite des operations:\n' +
            '   Parentheses -> Multiplications/Divisions -> Additions/Soustractions\n\n' +
            '2) Multiplication utile:\n' +
            '   7 x 8 = 56, 6 x 6 = 36\n\n' +
            '3) Fractions equivalentes:\n' +
            '   1/2 = 2/4 = 3/6\n\n' +
            '4) Racine carree:\n' +
            '   racine(81) = 9\n\n' +
            'Lis ces rappels puis lance le duel.', {
                font: '22px Arial',
                fill: '#ffffff',
                align: 'left',
                wordWrap: { width: 600 }
            }
        ).setOrigin(0.5, 0);

        this.duelButton = this.add.text(0, 0, '[ CONTINUER VERS LE DUEL ]', {
            font: '28px Arial',
            fill: '#00ff88'
        }).setOrigin(0.5).setInteractive({ useHandCursor: true });

        this.backButton = this.add.text(0, 0, '[ RETOUR AUX MATIERES ]', {
            font: '18px Arial',
            fill: '#aaaaaa'
        }).setOrigin(0.5).setInteractive({ useHandCursor: true });

        this.duelButton.on('pointerover', () => this.duelButton.setStyle({ fill: '#ffffff' }));
        this.duelButton.on('pointerout', () => this.duelButton.setStyle({ fill: '#00ff88' }));
        this.duelButton.on('pointerdown', () => this.scene.start('CharacterSelectScene', {
            subject: 'math',
            previousScene: 'MathCourseScene'
        }));

        this.backButton.on('pointerover', () => this.backButton.setStyle({ fill: '#ffffff' }));
        this.backButton.on('pointerout', () => this.backButton.setStyle({ fill: '#aaaaaa' }));
        this.backButton.on('pointerdown', () => this.scene.start('CourseScene'));

        this.input.keyboard.on('keydown-ESC', () => this.scene.start('CourseScene'));

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
        const titleSize = Phaser.Math.Clamp(Math.round(base * 0.07), 24, 50);
        const lessonSize = Phaser.Math.Clamp(Math.round(base * 0.03), 14, 24);
        const duelSize = Phaser.Math.Clamp(Math.round(base * 0.045), 18, 34);
        const backSize = Phaser.Math.Clamp(Math.round(base * 0.028), 14, 22);

        this.bg.setSize(width, height);

        this.titleText
            .setPosition(width / 2, height * 0.09)
            .setStyle({ font: `bold ${titleSize}px Arial` });

        this.lessonText
            .setPosition(width / 2, height * 0.17)
            .setStyle({ font: `${lessonSize}px Arial` })
            .setWordWrapWidth(Math.min(width * 0.86, 900));

        this.duelButton
            .setPosition(width / 2, height * 0.84)
            .setStyle({ font: `${duelSize}px Arial` });

        this.backButton
            .setPosition(width / 2, height * 0.92)
            .setStyle({ font: `${backSize}px Arial` });
    }
}
