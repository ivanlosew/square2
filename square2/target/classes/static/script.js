let userId;
let cursor;
get(4, function () {
    userId = this;
    post(4, parseInt(this) + parseInt(1));
});
let first = Boolean(true);
let isMoving = Boolean(false);
let internet = Boolean(true);
let isTyping = Boolean(false);
update();
/*window.addEventListener("beforeunload", function (e) {
    get(7, function () {
        let tmp = "";
        let newActiveUsersValue = "";
        for (let i = 0; i < this.length; i++) {
            if (this.charAt(i) == ",") {
                if (userId != parseInt(tmp))
                    newActiveUsersValue += tmp + ",";
                tmp = "";
                console.log(tmp);
            }
            else tmp += this.charAt(i);
        }
        post(7, newActiveUsersValue);
    })
});*/
document.getElementById("field").addEventListener("keydown", function (e) {
    let field = document.getElementById("field");
    let k = e.keyCode;
    if (k == 9 || (k >= 16 && k <= 17) || (k >= 37 && k <= 40) || k == 93 || e.ctrlKey || e.altKey || e.metaKey)
        return;
    if (e.keyCode == 8)
        postText(field.value, `-${field.selectionStart}`)
    else postText(field.value, `+${field.selectionStart},${e.key}`)
});
dragElement(document.getElementById(("edit")));

function setUserId() {
    post(6, userId);
    cursor = document.getElementById(("field")).selectionStart;
}

function beginTyping() {
    isTyping = true;
}

function stopTyping() {
    isTyping = false;
}

function test() {
    let random = Math.random() * document.getElementById("field").value.length;
    setTimeout(function() {
        test2(random,0);
        }, 500);
}

function test2 (random, i) {
    let word = "_olleh_";
    console.log("p");
    post(6, userId);
    document.getElementById("field").value = document.getElementById("field").value.substring(0, random) + word.toString().charAt(i) + document.getElementById("field").value.substring(random);
    if (i < 6) {
        setTimeout(function () {
            test2(random, i + 1);
        }, 500);
    } else setTimeout(test, 1000)
}

function merge(text1, text2) {
    let result = "";
    let j = 0;
    for (let i = 0; i < text2.length; i++) {
        if (text1.charAt(i) == text2.charAt(i))
            result += text1.charAt(i);
        else {
            for (; text1.charAt(j) != text2.charAt(i) && j < text1.length; j++) {
                result += text1.charAt(j);
            }
            for (; text1.charAt(j) != text2.charAt(i) && i < text2.length; i++) {
                result += text2.charAt(i);
            }
        }
        j++;
    }
    console.log(result);
    return result;
}


function update() {
    if (internet) {
        if (!isMoving || first) {
            get(1, function () {
                document.getElementById(("edit")).style.top = this;
            });
            get(2, function () {
                document.getElementById(("edit")).style.left = this;
            });
        }
        let field = document.getElementById("field");
        get(6, function () {
            const user = this;
            get(5, function () {
                let text = this;
                if (text != field.value && parseInt(user) == userId) {
                    //post(5, field.value); //toDelete
                    //post(userId, field.value);
                    //post(6, userId);
                }
                if (text != field.value && parseInt(user) != userId && !isTyping) {
                    let i;
                    for (i = 0; i < Math.max(field.value.length, text.length); i++) {
                        if (field.value.toString().charAt(i) != text.toString().charAt(i)) {
                            break;
                        }
                    }
                    cursor = field.selectionStart;
                    if (i <= cursor)
                        cursor += text.length - field.value.length;
                    field.value = text;
                    field.selectionStart = field.selectionEnd = cursor;
                    console.log(cursor);
                    if (first) {
                        first = false;
                    }

                }
                document.getElementById(("text")).innerHTML = text;
            });
            /*get(7, function () {
                if (!this.includes(userId.toString() + ",")) {
                    let newActiveUsersValue
                    if (this == "0")
                        newActiveUsersValue = userId.toString() + ",";
                    else newActiveUsersValue = this + userId.toString() + ",";
                    post(7, newActiveUsersValue);
                }
            })*/
        });
    }
    setTimeout(update, 500);
}
/*
Проблема:
Когда 2 пользователя пишут одновременно, буквы то у одного, то у другого пропадают. Это происходит из-за того,
что изменения не отправляются моментально, и не моментально отображаются у второго пользователи -- они как бы
редактируют две разные версии текста. Если один добавил букву "a", а в этот момент второй убрал букву "b",
то так как первое отправилось на сервер раньше, оно и станет новой версией для обоих пользователей.

Вариант решения: каким-либо образом мерджить строки.
Примеры:
1)
original: 123456789
user1: 12356789
user2: 1234567890
result: 123567890

2)
original: 123456789
user1: 1234456789
user2: 1234567890
result: 12344567890

детектить изменения, сранвивая с оригиналом, и в оригинал сохраняться с учётом всех изменений
по userid записывать их вариант каждый раз, а потом всё
 */

function postText(original, value) {
    const url = "http://localhost:8080/text";
    const params = "original=" + original + "&value=" + value;
    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if (xhr.readyState == 4 && xhr.responseText != "text")
            noConnection();
    };
    xhr.open("POST", url + "?" + params, true);
    xhr.send(params);
}

function post(id, value) {
    const url = "http://localhost:8080/add";
    const params = "id=" + id + "&value=" + value;
    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if (xhr.readyState == 4 && xhr.responseText != "add")
            noConnection();
    };
    xhr.open("POST", url + "?" + params, true);
    xhr.send(params);
}

function get(id, callback) {
    const url = "http://localhost:8080/get";
    const params = "id=" + id;
    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if (xhr.readyState == 4 && xhr.status == 200)
            callback.call(xhr.responseText);
    };
    xhr.open("GET", url + "?" + params, true);
    xhr.send();
}

function noConnection() {
    internet = false;
    document.getElementById("field").setAttribute("disabled", "disabled");
    document.getElementById("text").innerHTML = "Нет соединения с Интернетом";
    checkConnection();
}

function checkConnection() {
    const url = "http://localhost:8080/add";
    const params = "id=" + 3 + "&value=" + 0;
    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if (xhr.readyState == 4 && xhr.responseText == "add") {
            internet = true;
            document.getElementById("field").removeAttribute("disabled");
        }
        else setTimeout(checkConnection, 1000)
    };
    xhr.open("POST", url + "?" + params, true);
    xhr.send(params);
}

function dragElement(elmnt) {
    let pos1 = 0, pos2 = 0, pos3 = 0, pos4 = 0;
    document.getElementById("header").onmousedown = dragMouseDown;


    function dragMouseDown(e) {
        e = e || window.event;
        pos3 = e.clientX;
        pos4 = e.clientY;
        document.onmouseup = closeDragElement;
        document.onmousemove = elementDrag;
        document.getElementById("movecursor").onmousedown = null;
    }

    function elementDrag(e) {
        isMoving = true;
        e = e || window.event;
        pos1 = pos3 - e.clientX;
        pos2 = pos4 - e.clientY;
        pos3 = e.clientX;
        pos4 = e.clientY;
        elmnt.style.top = (elmnt.offsetTop - pos2) + "px";
        post(1, elmnt.style.top);
        elmnt.style.left = (elmnt.offsetLeft - pos1) + "px";
        post(2, elmnt.style.left);
    }

    function closeDragElement() {
        isMoving = false;
        document.onmouseup = null;
        document.onmousemove = null;
    }
}