module.exports = {
  extends: ['plugin:vue/vue3-recommended'],
  rules: {
    // 注释规则
    'lines-around-comment': [1, {
      allowBlockStart: true, // 注释出现在块语句的开始位置
      allowObjectStart: true, // 注释出现在对象字面量的开始位置
      allowArrayStart: true, // 注释出现在数组字面量的开始位置
      allowClassStart: true, // 注释出现在object块的开始位置
      beforeBlockComment: true, // 块注释之前空行
      beforeLineComment: true // 行注释之前空行
    }],

    // "camelcase": ["error"], //	强制使用骆驼拼写法命名约定
    "no-duplicate-imports": ['error'], // 禁止重复import
    "no-redeclare": ["error"], //	禁止多次声明同一变量
    "no-alert": ["error"], // 不准使用alert
    "no-loop-func": ["error"], // 禁止在循环语句中出现包含不安全引用的函数声明
    "no-dupe-keys": ["error"], // 禁止对象字面量中出现重复的 key
    "no-extra-parens": ["error"], // 禁止不必要的括号
    "no-duplicate-case": ["error"], // 禁止出现重复的 case 标签
    "no-dupe-args": ["error"], // 禁止 function 定义中出现重名参数
    "no-irregular-whitespace": ["error"], //禁止不规则的空白
    "no-unreachable": ['error'], // 禁止在 return、throw、continue 和 break 语句之后出现不可达代码
    // "require-atomic-updates": ['error'], // 禁止由于 await 或 yield的使用而可能导致出现竞态条件的赋值
    "block-scoped-var": ['error'], // 强制把变量的使用限制在其定义的作用域范围内
    'no-multiple-empty-lines': ["error", { "max": 1, "maxEOF": 0, 'maxBOF': 1 }],
    'keyword-spacing': ["error", { "before": true }], //关键字空格
    'comma-spacing': ["error", { "before": false, "after": true }], // 逗号后要有空格，前不能有空格
    "array-bracket-spacing": ['error', "never"], // 是否允许非空数组里面有多余的空格
    "comma-dangle": ['error', "never"], // 对象字面量项尾不能有逗号
    "key-spacing": ['error', { "beforeColon": false, "afterColon": true, "mode": "strict" }], // 对象字面量中冒号的前后空格
    // "newline-after-var": ['error'], // 变量声明后是否需要空一行
    "vue/no-spaces-around-equal-signs-in-attribute": ["error"], //等号前后要有空格
    "space-infix-ops": ["error", {
      "int32Hint": false
    }], // 符号与变量、常量之间必须有一个空格
    "space-in-parens": ["error", "never"],
    'eqeqeq': 'error', //  使用'==='来代替'=='
    'no-multi-spaces': 'error', // 禁止在单行内非缩进情况出现多个空格
    'no-trailing-spaces': 'error', // 禁止行尾空格
    'switch-colon-spacing': ['error', { 'after': true, 'before': false }], // switch 的冒号之后有空格
    'no-var': 'error',
    'space-before-function-paren': ["error", { "anonymous": "never", "named": "never", "asyncArrow": "always" }], // 函数名后不需要空格
    'no-empty-function': 'error', // 不允许使用空函数
    'default-case': 'error', // switch语句必须有default分支
    'indent': ['error', 2], // 统一缩进2个空格
    'semi': [2, 'never'], // 禁止尾部使用分号“ ; ”
    // 'quotes': [2, 'single'], // 使用单引号
    'vue/max-attributes-per-line': ['error', {
      singleline: 2, // 标签超出2个属性就会换行
      multiline: {
        max: 5
      }
    }],
    'vue/multi-word-component-names': 0,
    'vue/script-setup-uses-vars': 'error', // setup 语法糖校验
    'object-curly-spacing': ['error', 'always'], // 对象前后要加空格 { a: 1 }
    "object-shorthand": ["error", "always", {
      "avoidQuotes": true
    }],

    // 'array-bracket-spacing': [ 'error', 'always' ], // 数组前后要加空格 [ 1, 2 ]
    'array-bracket-newline': ['error', {
      'minItems': 5
    }], // 数组超过五个值可以换行
    'arrow-spacing': 'error', // 箭头函数前后加空格 () => {}
    // 'vue/no-unsupported-features': ['error', { // 校验不支持的特性
    //   'version': "^3.0.0",
    //   'ignores': [],
    // }]
    'vue/block-tag-newline': ['error', { //  标签直接的换行规范
      'singleline': 'always',
      'multiline': 'always',
      'maxEmptyLines': 0,
      'blocks': {
        'script': {
          'singleline': 'always',
          'multiline': 'always',
          'maxEmptyLines': 0
        },
        'template': {
          'singleline': 'always',
          'multiline': 'always',
          'maxEmptyLines': 0
        },
        'my-block': {
          'singleline': 'always',
          'multiline': 'always',
          'maxEmptyLines': 0
        }
      }
    }]

    // 'vue/no-unused-properties': ['error', { // 未使用的props， 数据， 和方法
    //   'groups': ['props', 'data', 'methods']
    // }]
  }
}