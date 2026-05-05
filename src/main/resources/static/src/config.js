import BootScene from './scenes/BootScene.js';
import PreloadScene from './scenes/PreloadScene.js';
import MenuScene from './scenes/MenuScene.js';
import CourseScene from './scenes/CourseScene.js';
import MathCourseScene from './scenes/MathCourseScene.js';
import FrenchCourseScene from './scenes/FrenchCourseScene.js';
import CharacterSelectScene from './scenes/CharacterSelectScene.js';
import GameScene from './scenes/GameScene.js';

const config = {
    type: Phaser.AUTO,
    width: window.innerWidth,
    height: window.innerHeight,
    resolution: Math.min(window.devicePixelRatio || 1, 2),
    autoRound: true,
    backgroundColor: '#1a1a2e',
    render: {
        antialias: true,
        pixelArt: false,
        roundPixels: true
    },
    scale: {
        mode: Phaser.Scale.RESIZE,
        autoCenter: Phaser.Scale.CENTER_BOTH
    },
    physics: {
        default: 'arcade',
        arcade: {
            gravity: { y: 0 },
            debug: false
        }
    },
    scene: [BootScene, PreloadScene, MenuScene, CourseScene, MathCourseScene, FrenchCourseScene, CharacterSelectScene, GameScene]
};

export default config;
