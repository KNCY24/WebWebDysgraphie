var page = 0

function nextPage(){
    page = page +1;
    changePage();
}

function previousPage() {
    page = page -1;
    changePage();
}

function changePage(){
    var mespages = document.getElementsByClassName("modal-content");
    for (var i = 0 ; i < mespages.length ; i++) {
        mespages[i].style.display="none";
    }
    mespages[page].style.display="block";
}
