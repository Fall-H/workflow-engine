export function conversionTypeNumberToType(typeNumber) {
    console.log(typeNumber)
    switch (typeNumber) {
        case 0:
            return 'input'
        case 1:
            return 'process'
        case 2:
            return 'judge'
        case 3:
            return 'output'
        default:
            return 'process'
    }
}

export function conversionTypeToTypeNumber(type) {
    switch (type) {
        case 'input':
            return 0
        case 'process':
            return 1
        case 'judge':
            return 2
        case 'output':
            return 3
        default:
            return -1
    }
}