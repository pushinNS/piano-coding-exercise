$(document).ready(function () {
  $("#registerForm").submit(function (event) {
    event.preventDefault();
    var json = $(this).serializeFormJSON();
    sendPost("/register", json);
  });
  $("#loginForm").submit(function (event) {
    event.preventDefault();
    var json = $(this).serializeFormJSON();
    sendPost("/login", json);
  });

  function sendPost(address, json) {
    $.ajax({
      url: address,
      type: "POST",
      contentType: "application/json; charset=utf-8",
      dataType: "json",
      data: JSON.stringify(json)
    })
  }

  (function ($) {
    $.fn.serializeFormJSON = function () {

      var o = {};
      var a = this.serializeArray();
      $.each(a, function () {
        if (o[this.name]) {
          if (!o[this.name].push) {
            o[this.name] = [o[this.name]];
          }
          o[this.name].push(this.value || '');
        } else {
          o[this.name] = this.value || '';
        }
      });
      return o;
    };
  })(jQuery);
});