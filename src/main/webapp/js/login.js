$(document).ready(function() {
  var hash = window.location.hash;
  var action = $("form").attr("action");
  $("form").attr('action', action + hash);
});
