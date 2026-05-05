import { SUBJECT_KEYS } from '../../utils/constants.js';

const QUESTION_BANK = {
    [SUBJECT_KEYS.MATH]: [
        { q: 'Combien font 7 x 8 ?', choices: ['54', '56', '58', '64'], a: 1 },
        { q: 'Quelle est la racine carree de 81 ?', choices: ['7', '8', '9', '10'], a: 2 },
        { q: 'Combien font 15 - 9 ?', choices: ['5', '6', '7', '8'], a: 1 },
        { q: 'Quelle fraction est equivalente a 1/2 ?', choices: ['2/3', '3/6', '4/10', '5/8'], a: 1 },
        { q: 'Combien font 12 + 13 ?', choices: ['23', '24', '25', '26'], a: 2 },
        { q: 'Combien font 6 x 6 ?', choices: ['30', '32', '34', '36'], a: 3 }
    ],
    [SUBJECT_KEYS.FRENCH]: [
        { q: 'Quel est le pluriel de cheval ?', choices: ['chevals', 'chevaux', 'chevaus', 'chevails'], a: 1 },
        { q: 'Choisis le verbe correctement conjugue.', choices: ['Nous mange', 'Nous manges', 'Nous mangeons', 'Nous manger'], a: 2 },
        { q: 'Quel mot est un adjectif ?', choices: ['courir', 'rapidement', 'bleu', 'maison'], a: 2 },
        { q: 'Quel est le contraire de heureux ?', choices: ['content', 'triste', 'joyeux', 'souriant'], a: 1 },
        { q: 'Dans "Les enfants jouent", le sujet est :', choices: ['jouent', 'enfants', 'Les enfants', 'les'], a: 2 },
        { q: 'Quel mot est bien orthographie ?', choices: ['apparament', 'apparement', 'apparemment', 'aparament'], a: 2 }
    ]
};

export function getQuestionsBySubject(subject) {
    const key = subject === SUBJECT_KEYS.FRENCH ? SUBJECT_KEYS.FRENCH : SUBJECT_KEYS.MATH;
    return QUESTION_BANK[key].map((question) => ({
        ...question,
        choices: [...question.choices]
    }));
}

export function loadQuestions(subject) {
    return getQuestionsBySubject(subject);
}
