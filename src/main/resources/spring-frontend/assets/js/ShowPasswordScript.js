$('body').on('click', '.password-control', function(){
    if ($('#phonecode').attr('type') == 'password'){
        $(this).addClass('view');
        $('#phonecode').attr('type', 'text');
    } else {
        $(this).removeClass('view');
        $('#phonecode').attr('type', 'password');
    }
    return false;
}).on('click', '.password-control', function() {
    if ($('#mailcode').attr('type') == 'password') {
        $(this).addClass('view');
        $('#mailcode').attr('type', 'text');
    } else {
        $(this).removeClass('view');
        $('#mailcode').attr('type', 'password');
    }
    return false;
});
