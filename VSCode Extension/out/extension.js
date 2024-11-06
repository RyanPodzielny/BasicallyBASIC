const vscode = require('vscode');
const path = require('path'); 

// Activates the extension, more specifically the run button in the status bar
// Help received from: 
//      https://code.visualstudio.com/api/get-started/your-first-extension
//      https://www.youtube.com/watch?v=cHQo26fdx_o
function activate() {
    let runCommand = vscode.commands.registerCommand('runbbasic.run', function () {
        // Ensure we are in a valid workspace
        const editor = vscode.window.activeTextEditor;
        if (editor) {
            // Get the name of the file and the directory it is in
            const filePath = editor.document.fileName;
            const directory = path.dirname(filePath);
            const document = path.basename(editor.document.fileName);

            // Create a new terminal to run from
            const terminal = vscode.window.createTerminal('BasicallyBASIC');
            // Enter the right directory and run the file
            terminal.sendText(`cd "${directory}"`);
            terminal.sendText(`bbasic ${document}`);
            terminal.show();
        }
    });
}

// Export the activate function to run
module.exports = {
    activate
};