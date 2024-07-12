
export const getAddRuleList = (type) => {
  if (type === 'SORT') {
    return [{
      label: '选择单一名词（按名词分类）',
      value: 'SORT'
    }]
  } else {
    return [{
      label: '选择单一名词（按范围分类）',
      value: 'RANGE'
    }]
  }
}