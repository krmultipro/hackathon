import { getGlobalLeaderboard } from '../modules/leaderboard/leaderboardService.js';

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

        this.leaderboardButton = this.add.text(0, 0, '[ CLASSEMENT ]', {
            font: '28px Arial',
            fill: '#56ccf2'
        }).setOrigin(0.5).setInteractive({ useHandCursor: true });

        this.logoutButton = this.add.text(0, 0, 'Deconnexion', {
            font: '20px Arial',
            fill: '#ff8a8a',
            backgroundColor: '#2a1020',
            padding: { x: 12, y: 8 }
        }).setOrigin(1, 0).setInteractive({ useHandCursor: true });

        this.leaderboardOverlay = this.add.rectangle(0, 0, width, height, 0x000000, 0.72)
            .setOrigin(0)
            .setDepth(20)
            .setVisible(false)
            .setInteractive();
        this.leaderboardPanel = this.add.rectangle(0, 0, Math.min(width * 0.78, 520), Math.min(height * 0.72, 520), 0x08151f, 0.96)
            .setStrokeStyle(2, 0x56ccf2, 0.6)
            .setDepth(21)
            .setVisible(false);
        this.leaderboardTitle = this.add.text(0, 0, 'Classement', {
            font: 'bold 30px Arial',
            fill: '#ffffff'
        }).setOrigin(0.5).setDepth(22).setVisible(false);
        this.leaderboardContent = this.add.text(0, 0, '', {
            font: '22px Arial',
            fill: '#dbe7f3',
            align: 'left',
            wordWrap: { width: Math.min(width * 0.6, 420) }
        }).setOrigin(0.5, 0).setDepth(22).setVisible(false);
        this.closeLeaderboardButton = this.add.text(0, 0, 'Fermer', {
            font: '20px Arial',
            fill: '#08151f',
            backgroundColor: '#56ccf2',
            padding: { x: 12, y: 8 }
        }).setOrigin(0.5).setDepth(22).setVisible(false).setInteractive({ useHandCursor: true });

        this.courseButton.on('pointerover', () => this.courseButton.setStyle({ fill: '#ffffff' }));
        this.courseButton.on('pointerout', () => this.courseButton.setStyle({ fill: '#00ff88' }));
        this.courseButton.on('pointerdown', () => this.scene.start('CourseScene'));

        this.tournamentButton.on('pointerover', () => this.tournamentButton.setStyle({ fill: '#ffffff' }));
        this.tournamentButton.on('pointerout', () => this.tournamentButton.setStyle({ fill: '#f2c94c' }));
        this.tournamentButton.on('pointerdown', () => this.scene.start('CharacterSelectScene', { previousScene: 'MenuScene' }));

        this.leaderboardButton.on('pointerover', () => this.leaderboardButton.setStyle({ fill: '#ffffff' }));
        this.leaderboardButton.on('pointerout', () => this.leaderboardButton.setStyle({ fill: '#56ccf2' }));
        this.leaderboardButton.on('pointerdown', () => {
            this.openLeaderboard();
        });

        this.logoutButton.on('pointerover', () => this.logoutButton.setStyle({ fill: '#ffffff' }));
        this.logoutButton.on('pointerout', () => this.logoutButton.setStyle({ fill: '#ff8a8a' }));
        this.logoutButton.on('pointerdown', () => {
            if (typeof window.logout === 'function') {
                window.logout();
            }
        });

        this.leaderboardOverlay.on('pointerdown', () => this.closeLeaderboard());
        this.closeLeaderboardButton.on('pointerdown', () => this.closeLeaderboard());

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
        const panelWidth = Math.min(width * 0.78, 520);
        const panelHeight = Math.min(height * 0.72, 520);

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
        this.leaderboardButton
            .setPosition(width / 2, height * 0.74)
            .setStyle({ font: `${Math.max(22, Math.round(btnSize * 0.85))}px Arial` });
        this.logoutButton
            .setPosition(width - 24, 24)
            .setStyle({ font: `${Math.max(16, Math.round(btnSize * 0.5))}px Arial` });
        this.leaderboardOverlay.setSize(width, height);
        this.leaderboardPanel
            .setSize(panelWidth, panelHeight)
            .setPosition(width / 2, height / 2);
        this.leaderboardTitle.setPosition(width / 2, height / 2 - panelHeight * 0.36);
        this.leaderboardContent
            .setPosition(width / 2, height / 2 - panelHeight * 0.24)
            .setWordWrapWidth(panelWidth - 70);
        this.closeLeaderboardButton.setPosition(width / 2, height / 2 + panelHeight * 0.38);
    }

    async openLeaderboard() {
        this._setLeaderboardVisibility(true);
        this.leaderboardContent.setText('Chargement du classement...');

        try {
            const players = await getGlobalLeaderboard();

            if (!players.length) {
                this.leaderboardContent.setText('Aucun joueur classe pour le moment.');
                return;
            }

            const lines = players.map((player, index) =>
                `${index + 1}. ${player.username} - ${player.globalElo || 0} ELO`
            );

            this.leaderboardContent.setText(lines.join('\n'));
        } catch (error) {
            this.leaderboardContent.setText(error.message || 'Impossible de charger le classement.');
        }
    }

    closeLeaderboard() {
        this._setLeaderboardVisibility(false);
    }

    _setLeaderboardVisibility(isVisible) {
        this.leaderboardOverlay.setVisible(isVisible);
        this.leaderboardPanel.setVisible(isVisible);
        this.leaderboardTitle.setVisible(isVisible);
        this.leaderboardContent.setVisible(isVisible);
        this.closeLeaderboardButton.setVisible(isVisible);
    }
}
