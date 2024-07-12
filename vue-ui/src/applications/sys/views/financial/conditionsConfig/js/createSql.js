
let mapOptions = null

const createSql = (expression, selectoptions = []) => {
  mapOptions = selectoptions // 获取字段的配置文件，根据配置文件中字段的类型来决定数据类型
  return handleString(expression)
}

const handleString = (expression) => {
  if (!expression.children) return // 一个children表示一个括号，主入口没有children就是空sql
  let res = ''
  const { children, connect: rootConnect } = expression
  children.forEach((child, index) => {
    if (!child.children) res += handleSingleE(child) // 没有嵌套children的表示单条件，直接处理
    else { // 反之表示有括号，递归处理
      res += `( ${handleString(child)} )`
    }
    if (index !== children.length - 1) res += ` ${rootConnect} `
  })
  return res
}

const handleSingleE = (val) => {
  let { key, value, operator } = val
  const index = mapOptions.findIndex(item => item.value === key) // 找到该字段在配置文件中的索引
  if (operator === '[!]IN'){ // 如果是in条件，需要渲染成这样 a [!]IN( a , b )
    if (typeof value !== 'object') value = [value]
    if (index !== -1 && mapOptions[index].type) return `${key} ${operator}( ${value.join(', ')} )` // 如果该字段有type，那就直接返回
    value = value.map(item => { // 反之需要加上单引号再返回
      if (item.startsWith(`'`) && item.endsWith(`'`)) return item
      else return `'${item}'`
    })
    return `${key} ${operator}( ${value.join(', ')} )`
  }
  if (value === 'null' && (operator === '[!]isNot' || operator === '[!]is')) return `${key} ${operator} ${value}`
  else if (!value && typeof value !== 'string') return `${key} ${operator} ${value}`
  return `${key} ${operator} '${value}'`
}

export default createSql