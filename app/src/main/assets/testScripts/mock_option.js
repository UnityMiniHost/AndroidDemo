console.log("[Mock Game] mock_option.js starts");

setTimeout(() => {
    const launchOptions = tj.getLaunchOptionsSync();
    console.log("[Mock Game] [MapleLeaf] invoke tj.getLaunchOptionsSync: " + JSON.stringify(launchOptions, null, 2));
}, 30000);

setTimeout(() => {
    const launchOptions = tj.getEnterOptionsSync();
    console.log("[Mock Game] [MapleLeaf] invoke tj.getEnterOptionsSync: " + JSON.stringify(launchOptions, null, 2));
}, 60000);
