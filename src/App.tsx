//import UploadFiles from "./components/upload-files.component";
import UploadFiles from "./webapp/components/UploadFiles";
import React from "react";
import {Tab} from "./webapp/components/Tab";
import {Tabs} from "./webapp/components/Tabs";

export default class App extends React.Component<any, any> {
    documentManagerElement = (<div className="container" style={{ width: "500px" }}>
        <div>
            <h3>My File Uploader</h3>
        </div>

        <UploadFiles />
    </div>);

    public render() {

        return (
            <div id={"app"}>
                <Tabs>
                    <Tab title="Documents">{this.documentManagerElement}</Tab>
                    <Tab title="Test Tab">This is Test Tab Content</Tab>
                </Tabs>
                </div>
        );
    }
}