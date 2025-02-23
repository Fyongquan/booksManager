// 设置cookie
export function setCookie (name, value, days = 7) {
  const date = new Date()
  date.setTime(date.getTime() + days * 24 * 60 * 60 * 1000)
  const expires = `expires=${date.toUTCString()}`
  document.cookie = `${name}=${encodeURIComponent(value)};${expires};path=/`
}

// 获取cookie
export function getCookie (name) {
  const cookieName = `${name}=`
  const cookies = document.cookie.split(';')
  for (let cookie of cookies) {
    cookie = cookie.trim()
    if (cookie.indexOf(cookieName) === 0) {
      return decodeURIComponent(cookie.substring(cookieName.length))
    }
  }
  return ''
}

// 删除cookie
export function removeCookie (name) {
  setCookie(name, '', -1)
} 