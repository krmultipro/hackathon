export default class MenuScene extends Phaser.Scene {
    constructor() {
        super({ key: 'MenuScene' });
    }

    create() {
        const { width, height } = this.scale;

        this.bg = this.add.rectangle(0, 0, 1, 1, 0x101626).setOrigin(0);

        this.titleText = this.add.text(0, 0, 'EducArena', {
            font: 'bold 48px Arial',
            fill: '#ffffff'
        }).setOrigin(0.5);

        this.courseButton = this.add.text(0, 0, '[ COURS ]', {
            font: '32px Arial',
            fill: '#00ff88'
        }).setOrigin(0.5).setInteractive({ useHandCursor: true });

        this.tournamentButton = this.add.text(0, 0, '[ TOURNOI ]', {
            font: '32px Arial',
            fill: '#f2c94c'
        }).setOrigin(0.5).setInteractive({ useHandCursor: true });

        this.logoutButton = this.add.text(0, 0, 'Deconnexion', {
            font: '20px Arial',
            fill: '#ff8a8a',
            backgroundColor: '#2a1020',
            padding: { x: 12, y: 8 }
        }).setOrigin(1, 0).setInteractive({ useHandCursor: true });

        this.courseButton.on('pointerover', () => this.courseButton.setStyle({ fill: '#ffffff' }));
        this.courseButton.on('pointerout', () => this.courseButton.setStyle({ fill: '#00ff88' }));
        this.courseButton.on('pointerdown', () => this.scene.start('CourseScene'));

        this.tournamentButton.on('pointerover', () => this.tournamentButton.setStyle({ fill: '#ffffff' }));
        this.tournamentButton.on('pointerout', () => this.tournamentButton.setStyle({ fill: '#f2c94c' }));
        this.tournamentButton.on('pointerdown', () => this.scene.start('CharacterSelectScene', { previousScene: 'MenuScene' }));

        this.logoutButton.on('pointerover', () => this.logoutButton.setStyle({ fill: '#ffffff' }));
        this.logoutButton.on('pointerout', () => this.logoutButton.setStyle({ fill: '#ff8a8a' }));
        this.logoutButton.on('pointerdown', () => {
            if (typeof window.logout === 'function') {
                window.logout();
            }
        });

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
        const base = Math.min(width, height);
        const titleSize = Phaser.Math.Clamp(Math.round(base * 0.09), 30, 64);
        const btnSize = Phaser.Math.Clamp(Math.round(base * 0.06), 24, 44);

        this.bg.setSize(width, height);
        this.titleText
            .setPosition(width / 2, height * 0.32)
            .setStyle({ font: `bold ${titleSize}px Arial` });
        this.courseButton
            .setPosition(width / 2, height * 0.50)
            .setStyle({ font: `${btnSize}px Arial` });
        this.tournamentButton
            .setPosition(width / 2, height * 0.62)
            .setStyle({ font: `${btnSize}px Arial` });
        this.logoutButton
            .setPosition(width - 24, 24)
            .setStyle({ font: `${Math.max(16, Math.round(btnSize * 0.5))}px Arial` });
    }
}
