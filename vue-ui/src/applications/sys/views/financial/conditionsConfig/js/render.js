import { ElIcon, ElSelect, ElOption, ElButton, ElInput } from 'element-plus'
import { Plus, DeleteFilled } from '@element-plus/icons-vue'
import { h, ref } from 'vue'
import datePicker from '../components/datePicker.vue'

let selectOptions = []

const operatorOptions = [
  {
    value: '>',
    label: '大于'
  },
  {
    value: '<',
    label: '小于'
  },
  {
    value: '=',
    label: '等于'
  },
  {
    value: '!=',
    label: '不等于'
  },
  {
    value: '[!]IN',
    label: '包括在'
  }, {
    value: '[!]is',
    label: '是'
  }, {
    value: '[!]isNot',
    label: '不是'
  }, {
    value: '[!]like',
    label: '像'
  }, {
    value: '[!]NotLike',
    label: '不像'
  }
]

const addSingleInternal = (val) => {
  val.children.push({
    key: '',
    value: '',
    operator: ''
  })
}

const addSetInternal = (val, command) => {

  val.children.push({
    children: [{
      key: '',
      value: '',
      operator: ''

    }, {
      key: '',
      value: '',
      operator: ''
    }],
    connect: command
  })
}

const deleteInternal = (val, father, index) => {
  father.splice(index, 1)

}

const expressionRender = ({ expression, selectoptions }) => {
  selectOptions = selectoptions
  return h('div', { class: 'list' },
    [h('div', { class: 'list-left' }, expression.connect === '[!]AND' ? '且' : '或'),
      h('div', { class: 'list-right' }, expressionRenderChildren(expression.children))])
}

const expressionInternalRender = ({ expression, father, index }) => {
  return h('div', { class: 'list mb10' },
    [h('div', { class: 'list-left' }, expression.connect === '[!]AND' ? '且' : '或'),
      h('div', { class: 'list-right-Internal' }, expressionRenderChildren(expression.children)),
      h('div', { class: 'list-right-handle' }, [h(ElIcon, { style: 'margin-bottom:8px;cursor: pointer', title: '添加单条件', onClick: addSingleInternal.bind(this, expression) }, () => {
        return h(Plus)
      }), h('div', { class: 'and-pic', style: expression.connect === '[!]AND' ? 'display:none' : '', title: '添加且集条件', onClick: addSetInternal.bind(this, expression, '[!]AND') }),
      h('div', { class: 'or-pic', style: expression.connect === '[!]OR' ? 'display:none' : '', title: '添加或集条件', onClick: addSetInternal.bind(this, expression, '[!]OR') }),
      h(ElIcon, { style: 'color:red;cursor: pointer;', title: '删除整个', onClick: deleteInternal.bind(this, expression, father, index) }, () => {
        return h(DeleteFilled)
      })])]
  )
}

const expressionRenderChildren = (expressionChildren) => {
  if (!expressionChildren) return
  return Array.from({ length: expressionChildren.length }, (v, i) => {
    if (!expressionChildren[i].children) return expressContentRender(expressionChildren[i], expressionChildren, i)
    else return expressionInternalRender({ expression: expressionChildren[i], father: expressionChildren, index: i })
  })

}

const expressContentRender = (e, father, idx) => {
  const index = selectOptions.findIndex(item => item.value === e.key)
  const isTime = index !== -1 && selectOptions[index].type === 'time'
  return h('div', { class: "mb10" }, [h(ElSelect, {
    modelValue: e.key,
    class: 'mr10',
    style: 'width:180px',
    filterable: true,
    filtermethod: (val) => {
      console.log(val)
      return []
    },
    placeholder: '请选择', onChange: (val) => {
      e.key = val
      if (e.operator === '[!]IN')
        e.value = []
      else e.value = null
    }
  }, () => {
    return selectionsRender({ selectOptions })
  }), h(ElSelect, {
    modelValue: e.operator,
    class: 'mr10',
    style: 'width:100px',
    placeholder: '请选择', onChange: (val) => {
      if (e.value){
        if (val === '[!]IN') e.value = []
        else if (typeof e.value === 'object') e.value = ''
      }
      e.operator = val
      console.log(e.value)
    } }, () => {
    return selectionsRender({ selectOptions: operatorOptions })
  }), valueEl(e.operator, e, isTime), h(ElButton, {
    style: "font-size:28px",
    type: "danger",
    icon: "CircleClose",
    circle: true,
    size: 'small',
    title: '删除该条件',
    onClick: () => {
      father.splice(idx, 1)
    }
  })])
}

const valueEl = (type, e, isTime) => {
  if (isTime){
    return h(datePicker, {
      value: +e.value,
      onValueChange: (val) => {
        e.value = val
      } })
  }
  if (type !== '[!]IN') {
    return h(ElInput, { clearable: true, modelValue: e.value, style: 'width:200px', placeholder: '请输入', class: 'mr10', onInput: (val) => {
      e.value = val
    } })
  }
  else return h(ElSelect, {
    modelValue: e.value,
    style: 'width:200px',
    placeholder: '请选择',
    multiple: true,
    allowCreate: true,
    filterable: true,
    collapseTags: true,
    collapseTagsTooltip: true,
    clearable: true,
    class: 'mr10',
    maxCollapseTags: 1, onChange: (val) => {
      e.value = val
    } }, () => {
    let index = selectOptions.findIndex(item => item.value === e.key)
    let options = index !== -1 ? selectOptions[index].options ? selectOptions[index].options : [] : []
    return selectionsRender({ selectOptions: options })
  })
}

const selectionsRender = ({ selectOptions }) => {
  return Array.from({ length: selectOptions.length }, (v, i) => {
    return h(ElOption, { key: selectOptions[i].value, label: selectOptions[i].label, value: selectOptions[i].value })
  })
}

export default expressionRender