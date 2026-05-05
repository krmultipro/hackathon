export default class PreloadScene extends Phaser.Scene {
    constructor() {
        super({ key: 'PreloadScene' });
    }

    preload() {
        this._createProgressBar();

        this.load.image('p1boy',  'assets/joueur1/garcon bleu transparent.png');
        this.load.image('p1girl', 'assets/joueur1/fille bleu transparent.png');
        this.load.image('p2boy',  'assets/joueur2/garcon rouge transparent.png');
        this.load.image('p2girl', 'assets/joueur2/fille rouge transparent.png');
        this.load.image('sceneBg', 'assets/scene_background/scene_background 2.png');
    }

    create() {
        this.scene.start('MenuScene');
    }

    _createProgressBar() {
        const width = this.cameras.main.width;
        const height = this.cameras.main.height;

        const progressBox = this.add.graphics();
        const progressBar = this.add.graphics();

        progressBox.fillStyle(0x222222, 0.8);
        progressBox.fillRect(width / 2 - 160, height / 2 - 25, 320, 50);

        const loadingText = this.add.text(width / 2, height / 2 - 50, 'Chargement...', {
            font: '20px monospace',
            fill: '#ffffff'
        }).setOrigin(0.5);

        this.load.on('progress', (value) => {
            progressBar.clear();
            progressBar.fillStyle(0x00ff88, 1);
            progressBar.fillRect(width / 2 - 150, height / 2 - 15, 300 * value, 30);
        });

        this.load.on('complete', () => {
            progressBar.destroy();
            progressBox.destroy();
            loadingText.destroy();
        });
    }
}
