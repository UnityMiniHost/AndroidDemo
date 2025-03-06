console.log("[Mock Game] mock_ui.js starts");

begin = 10;

// Show Toast at 10s
setTimeout(() => {
    tj.showToast({
        title: "来自小游戏的消息提醒",
        duration: 10 * 1000
    });
}, begin * 1000);

begin += 2;
setTimeout(() => {
    tj.showToast({
        title: "来自小游戏的第二条消息提醒，这条消息提醒很长，这条消息提醒很长，这条消息提醒很长，这条消息提醒很长，这条消息提醒很长",
        duration: 10 * 1000
    });
}, begin * 1000);

// Hide Toast at 12s
begin += 4;
setTimeout(() => {
    tj.hideToast();
}, begin * 1000);

// Show Loading at 15s
begin += 1;
setTimeout(() => {
    tj.showLoading({
        title: "来自小游戏的 LOADING..."
    });
}, begin * 1000);

// Hide Loading at 20s
begin += 4;
setTimeout(() => {
    tj.hideLoading();
}, begin * 1000);

// Show Modal
begin += 1;
setTimeout(() => {
    tj.showModal({
        title: "模态对话框",
        content: "提示的内容",
        showCancel: false,
        editable: false,
        placeholderText: "请输入",
        success: (res) => {
            if (res.confirm) {
                console.log("[MapleLeaf] 用户点击确定");
            } else if (res.cancel) {
                console.log("[MapleLeaf] 用户点击取消");
            }
            console.log("[MapleLeaf] res.content: " + res.content);
        },
        fail: (res) => {
            console.log("[MapleLeaf] tj.showModal fail" + res.errMsg);
        },
        complete: () => {
            console.log("[MapleLeaf] tj.showModal complete");
        }
    });
}, begin * 1000);

// Show Action Sheet
begin += 10;
setTimeout(() => {
    tj.showActionSheet({
        alertText: "显示操作菜单",
        itemList: ['选项 A', '选项 B', '选项 C'],
        success (res) {
            console.log("[MapleLeaf] res.tapIndex: " + res.tapIndex);
        },
        fail (res) {
            console.log("[MapleLeaf] tj.showActionSheet fail" + res.errMsg);
        },
        complete () {
            console.log("[MapleLeaf] tj.showActionSheet complete");
        }
    });
}, begin * 1000);