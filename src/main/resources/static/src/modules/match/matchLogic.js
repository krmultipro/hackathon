export function createMatchState({ maxHp, timePerQuestion, questions }) {
    return {
        maxHp,
        timePerQuestion,
        questions,
        leftHp: maxHp,
        rightHp: maxHp,
        timeLeft: timePerQuestion,
        roundLocked: false,
        leftAnswer: null,
        rightAnswer: null,
        currentQuestion: null,
        feedbackParts: [],
        winner: null
    };
}

export function startRound(state, randomPicker = Math.random) {
    state.roundLocked = false;
    state.timeLeft = state.timePerQuestion;
    state.leftAnswer = null;
    state.rightAnswer = null;
    state.feedbackParts = [];
    state.currentQuestion = pickRandomQuestion(state.questions, randomPicker);
    return state;
}

export function submitAnswer(state, side, answerIndex) {
    if (state.roundLocked) {
        return false;
    }

    if (side === 'left' && state.leftAnswer === null) {
        state.leftAnswer = answerIndex;
        return true;
    }

    if (side === 'right' && state.rightAnswer === null) {
        state.rightAnswer = answerIndex;
        return true;
    }

    return false;
}

export function tick(state) {
    if (state.roundLocked) {
        return { resolved: false, gameOver: isGameOver(state) };
    }

    state.timeLeft -= 1;
    if (state.timeLeft <= 0) {
        return resolveRound(state);
    }

    return { resolved: false, gameOver: false };
}

export function shouldResolveRound(state) {
    return state.leftAnswer !== null && state.rightAnswer !== null;
}

export function resolveRound(state) {
    if (state.roundLocked || !state.currentQuestion) {
        return { resolved: false, gameOver: isGameOver(state) };
    }

    state.roundLocked = true;
    state.feedbackParts = [];

    const leftCorrect = state.leftAnswer === state.currentQuestion.a;
    const rightCorrect = state.rightAnswer === state.currentQuestion.a;

    if (state.leftAnswer === null) {
        state.feedbackParts.push('Gauche: temps ecoule');
    } else if (leftCorrect) {
        state.rightHp = Math.max(0, state.rightHp - 1);
        state.feedbackParts.push('Gauche: bonne reponse, 1 degat');
    } else {
        state.feedbackParts.push('Gauche: mauvaise reponse');
    }

    if (state.rightAnswer === null) {
        state.feedbackParts.push('Droite: temps ecoule');
    } else if (rightCorrect) {
        state.leftHp = Math.max(0, state.leftHp - 1);
        state.feedbackParts.push('Droite: bonne reponse, 1 degat');
    } else {
        state.feedbackParts.push('Droite: mauvaise reponse');
    }

    if (state.leftHp <= 0 || state.rightHp <= 0) {
        state.winner = getWinnerLabel(state);
    }

    return {
        resolved: true,
        gameOver: isGameOver(state),
        leftTookDamage: rightCorrect,
        rightTookDamage: leftCorrect,
        feedbackText: getFeedbackText(state)
    };
}

export function isGameOver(state) {
    return state.leftHp <= 0 || state.rightHp <= 0;
}

export function getFeedbackText(state) {
    return state.feedbackParts.join(' | ');
}

export function getPlayerStateText(state) {
    const leftState = state.leftAnswer === null ? 'en attente' : 'valide';
    const rightState = state.rightAnswer === null ? 'en attente' : 'valide';
    return `J1: ${leftState}  |  J2: ${rightState}`;
}

export function getWinnerLabel(state) {
    if (state.leftHp > state.rightHp) {
        return 'Joueur gauche';
    }

    if (state.rightHp > state.leftHp) {
        return 'Joueur droite';
    }

    return 'Egalite';
}

function pickRandomQuestion(questions, randomPicker) {
    if (!Array.isArray(questions) || questions.length === 0) {
        return null;
    }

    const index = Math.floor(randomPicker() * questions.length);
    return questions[index];
}

