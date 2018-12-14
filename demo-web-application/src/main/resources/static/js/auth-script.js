window.onload = function () {
  setTimeout(redirect, 5000);

  function redirect() {
    var redirectUrl = document.getElementById("redirectUrlHolder").content;
    var param = document.getElementById("jwt").content;
    window.location.replace(redirectUrl + param);
  }

  function logout() {
    var XHR = new XMLHttpRequest();
    XHR.open("GET", "/logout");
    var headerName = getContentById("headerName");
    var headerPrefix = getContentById("headerPrefix") + " ";
    var headerValue = headerPrefix + getContentById("jwt");
    XHR.setRequestHeader(headerName, headerValue);
    XHR.send()
  }

  function getContentById(id) {
    return document.getElementById(id).content;
  }
}