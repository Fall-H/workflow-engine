export function jsonToXml(jsonData) {
  console.log(jsonData)
  // 递归函数，用于将 JSON 对象转换为 XML 字符串
  function toXml(obj, rootElementName = 'root') {
    let xml = '';

    // 如果是对象
    if (typeof obj === 'object' && !Array.isArray(obj)) {
      xml += `<${rootElementName}>`;
      for (const key in obj) {
        if (obj.hasOwnProperty(key)) {
          const value = obj[key];
          if (key === 'params' && typeof value === 'object') {
            // 特殊处理 params 对象
            xml += `<${key}>`;
            for (const paramKey in value) {
              if (value.hasOwnProperty(paramKey)) {
                xml += `<entry><key>${paramKey}</key><value>${value[paramKey]}</value></entry>`;
              }
            }
            xml += `</${key}>`;
          } else {
            xml += toXml(value, key);
          }
        }
      }
      xml += `</${rootElementName}>`;
    }
    // 如果是数组
    else if (Array.isArray(obj)) {
      for (const item of obj) {
        xml += toXml(item, rootElementName);
      }
    }
    // 如果是基本类型（字符串、数字、布尔值等）
    else {
      xml += `<${rootElementName}>${String(obj)}</${rootElementName}>`;
    }

    return xml;
  }

  // 添加 XML 声明
  let xmlString = '<?xml version="1.0" encoding="UTF-8"?>\n';
  xmlString += '<propertyValues>' + toXml(jsonData, 'propertyValue') + '</propertyValues>';

  return xmlString;
}

export function downloadXmlFile(xmlString, fileName) {
  if (typeof xmlString !== 'string') {
    console.error('Expected a string for xmlString, but got:', xmlString);
    return;
  }
  // 创建一个 Blob 对象
  const blob = new Blob([xmlString], { type: 'application/xml' });

  // 创建一个下载链接
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = fileName || 'data.xml';
  a.click();

  // 释放对象 URL
  URL.revokeObjectURL(url);
}

export function xmlToJson(xmlString) {
  // 解析XML字符串
  let parser = new DOMParser();
  let xmlDoc = parser.parseFromString(xmlString, "application/xml");

  // 转换函数
  function convertXmlToJson(node) {
      let obj = {};

      // 处理元素节点
      if (node.nodeType === 1) {
          // 处理属性
          if (node.attributes.length > 0) {
              obj["@attributes"] = {};
              for (let j = 0; j < node.attributes.length; j++) {
                  let attribute = node.attributes[j];
                  obj["@attributes"][attribute.nodeName] = attribute.nodeValue;
              }
          }
      } else if (node.nodeType === 3) { // 文本节点
          obj = node.nodeValue.trim();
      }

      // 处理子节点
      if (node.childNodes.length > 0) {
          for (let i = 0; i < node.childNodes.length; i++) {
              let item = node.childNodes[i];
              let nodeName = item.nodeName;
              if (typeof(obj[nodeName]) === "undefined") {
                  obj[nodeName] = convertXmlToJson(item);
              } else {
                  if (typeof(obj[nodeName].push) === "undefined") {
                      let old = obj[nodeName];
                      obj[nodeName] = [];
                      obj[nodeName].push(old);
                  }
                  obj[nodeName].push(convertXmlToJson(item));
              }
          }
      }
      return obj;
  }

  return convertXmlToJson(xmlDoc);
}