import { SUBJECT_KEYS } from '../../utils/constants.js';

const QUESTION_BANK = {
    [SUBJECT_KEYS.MATH]: [
        { q: 'Combien font 7 x 8 ?', choices: ['54', '56', '58', '64'], a: 1 },
        { q: 'Quelle est la racine carree de 81 ?', choices: ['7', '8', '9', '10'], a: 2 },
        { q: 'Combien font 15 - 9 ?', choices: ['5', '6', '7', '8'], a: 1 },
        { q: 'Quelle fraction est equivalente a 1/2 ?', choices: ['2/3', '3/6', '4/10', '5/8'], a: 1 },
        { q: 'Combien font 12 + 13 ?', choices: ['23', '24', '25', '26'], a: 2 },
        { q: 'Combien font 6 x 6 ?', choices: ['30', '32', '34', '36'], a: 3 },
        { q: 'Quelle fraction est la plus grande ?', choices: ['1/2', '3/8', 'Elles sont egales', 'Impossible a savoir'], a: 0 },
        { q: 'Dans 3/8, quel nombre indique les parts totales ?', choices: ['3', '8', '11', '0'], a: 1 },
        { q: 'Combien font 47 + 23 ?', choices: ['60', '68', '70', '72'], a: 2 },
        { q: 'Combien font 58 + 24 ?', choices: ['72', '82', '84', '86'], a: 1 },
        { q: '98 + 51 est proche de ...', choices: ['120', '150', '200', '250'], a: 1 },
        { q: 'Combien font 30 - 8 ?', choices: ['18', '20', '22', '24'], a: 2 },
        { q: 'Combien font 3 paquets de 4 feutres ?', choices: ['7', '8', '12', '16'], a: 2 },
        { q: '1/2 est equivalent a ...', choices: ['2/4', '2/3', '3/5', '4/7'], a: 0 },
        { q: 'Combien font 9 x 4 ?', choices: ['32', '36', '40', '44'], a: 1 },
        { q: 'Quelle operation utiliser si une quantite augmente ?', choices: ['Addition', 'Soustraction', 'Division', 'Aucune'], a: 0 },
        { q: 'Dans un probleme, que faut-il chercher en premier ?', choices: ['Le plus grand nombre', 'La question posee', 'La reponse finale', 'Le sujet'], a: 1 },
        { q: 'Combien font 100 - 37 ?', choices: ['53', '57', '63', '67'], a: 2 },
        { q: 'Quelle est la racine carree de 49 ?', choices: ['5', '6', '7', '8'], a: 2 },
        { q: 'Si 8 eleves descendent d un bus de 30 eleves, il reste ...', choices: ['18', '20', '22', '24'], a: 2 }
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
