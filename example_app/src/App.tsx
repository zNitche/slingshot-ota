import { useEffect, useState } from "react";
import classes from "./App.module.css";
import { SlingshotUpdater } from "slingshot-updater";

export default function App() {
    const [revision, setRevision] = useState<undefined | string>();

    useEffect(() => {
        SlingshotUpdater.get_revision_number().then((r) => setRevision(r.value))
    })

    return (
        <div className={classes.app}>
            <div className={classes.content}>
                <span>Slingshot Updater Test App</span>
                {revision !== undefined && <span>revision: {revision}</span>}
            </div>
        </div>
    )
}
