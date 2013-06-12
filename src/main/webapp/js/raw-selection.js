define([],
  function() {

    var rawSelection = function() {
      if (window.getSelection) {
        selection = window.getSelection();
      } else if (document.getSelection) {
        selection = document.getSelection();
      } else {
        selection = document.selection.createRange().text;
      }

      return selection;
    }

    return rawSelection;
});
