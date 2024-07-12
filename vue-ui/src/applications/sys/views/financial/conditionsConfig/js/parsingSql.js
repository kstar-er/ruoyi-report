
const parsingExpressions = (expressions, expressionType) => {
  const returnData = {
    children: [],
    connect: expressionType
  }
  let handle = expressions.split(` ${expressionType} `)
  while (true){
    for (let i = 0; i < handle.length; i++){
      if (handle[i] === '') continue
      if (handle[i].startsWith('(') && !handle[i].endsWith(')')){
        handle[i] = handle[i].trim() + ` ${expressionType} ` + handle[i + 1].trim()
        handle[i + 1] = ''
      }
    }
    if (handle.findIndex(item => item.length === 0) !== -1) handle = handle.filter(item => item.length > 0)
    else break
  }
  handle.forEach(expressionsItem => {
    if (expressionsItem.startsWith('(') && expressionsItem.endsWith(')')){
      returnData.children.push(parsingExpressions(expressionsItem.slice(2, expressionsItem.length - 2), returnData.connect === '[!]OR' ? '[!]AND' : '[!]OR'))
    }
    else returnData.children.push(handleExpressionsItem(expressionsItem.trim()))
  })

  return returnData
}

const handleExpressionsItem = (val) => {
  if (val.indexOf('[!]IN') !== -1){
    let [key, value] = val.split(' [!]IN')
    value = value.slice(2, value.length - 2).split(', ').map(item => {
      if (item.startsWith(`'`) && item.endsWith(`'`)) return item.slice(1, item.length - 1)
      else return item
    })
    return {
      key, operator: '[!]IN', value
    }
  } else {
    let [key, operator, value] = val.split(' ')
    if (value.startsWith(`'`) && value.endsWith(`'`)) value = value.slice(1, value.length - 1)
    return {
      key, operator, value
    }
  }
}

export default parsingExpressions