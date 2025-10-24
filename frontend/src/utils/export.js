/**
 * 数据导出工具
 * 支持将表格数据导出为CSV格式
 */

/**
 * 将数据导出为CSV文件
 * @param {Array} data - 要导出的数据数组
 * @param {Array} columns - 列配置数组，格式: [{ label: '列名', prop: '字段名' }]
 * @param {String} filename - 文件名
 */
export function exportToCSV(data, columns, filename = 'export.csv') {
  if (!data || data.length === 0) {
    console.warn('没有数据可导出')
    return
  }

  // 构建CSV内容
  const csvContent = []

  // 添加表头
  const headers = columns.map(col => col.label)
  csvContent.push(headers.join(','))

  // 添加数据行
  data.forEach(row => {
    const rowData = columns.map(col => {
      let value = row[col.prop]

      // 处理格式化函数
      if (col.formatter && typeof col.formatter === 'function') {
        value = col.formatter(row)
      }

      // 处理特殊字符
      if (value === null || value === undefined) {
        value = ''
      } else if (typeof value === 'string') {
        // 转义双引号并用双引号包裹包含逗号、换行或双引号的值
        value = value.replace(/"/g, '""')
        if (value.includes(',') || value.includes('\n') || value.includes('"')) {
          value = `"${value}"`
        }
      }

      return value
    })
    csvContent.push(rowData.join(','))
  })

  // 添加BOM头以支持Excel打开中文
  const BOM = '\uFEFF'
  const csvString = BOM + csvContent.join('\n')

  // 创建Blob并下载
  const blob = new Blob([csvString], { type: 'text/csv;charset=utf-8;' })
  downloadFile(blob, filename)
}

/**
 * 将数据导出为Excel格式 (使用简化的HTML table方法)
 * @param {Array} data - 要导出的数据数组
 * @param {Array} columns - 列配置数组
 * @param {String} filename - 文件名
 */
export function exportToExcel(data, columns, filename = 'export.xls') {
  if (!data || data.length === 0) {
    console.warn('没有数据可导出')
    return
  }

  let html = '<html><head><meta charset="utf-8"></head><body><table border="1">'

  // 添加表头
  html += '<thead><tr>'
  columns.forEach(col => {
    html += `<th>${col.label}</th>`
  })
  html += '</tr></thead>'

  // 添加数据行
  html += '<tbody>'
  data.forEach(row => {
    html += '<tr>'
    columns.forEach(col => {
      let value = row[col.prop]

      // 处理格式化函数
      if (col.formatter && typeof col.formatter === 'function') {
        value = col.formatter(row)
      }

      if (value === null || value === undefined) {
        value = ''
      }

      html += `<td>${value}</td>`
    })
    html += '</tr>'
  })
  html += '</tbody></table></body></html>'

  // 创建Blob并下载
  const blob = new Blob([html], { type: 'application/vnd.ms-excel' })
  downloadFile(blob, filename)
}

/**
 * 下载文件
 * @param {Blob} blob - 文件内容
 * @param {String} filename - 文件名
 */
function downloadFile(blob, filename) {
  const link = document.createElement('a')
  if (link.download !== undefined) {
    const url = URL.createObjectURL(blob)
    link.setAttribute('href', url)
    link.setAttribute('download', filename)
    link.style.visibility = 'hidden'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    URL.revokeObjectURL(url)
  }
}

/**
 * 格式化日期时间
 * @param {String} dateTimeStr - 日期时间字符串
 * @returns {String} 格式化后的日期时间
 */
export function formatDateTime(dateTimeStr) {
  if (!dateTimeStr) return ''
  const date = new Date(dateTimeStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

/**
 * 格式化日期
 * @param {String} dateStr - 日期字符串
 * @returns {String} 格式化后的日期
 */
export function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}
