import { useEffect } from "react";
import classes from "./App.module.css";
import { SlingshotUpdater } from "slingshot-updater";

export default function App() {
    useEffect(() => {
        const c = setInterval(() => { 
            SlingshotUpdater.echo({ value: "slingshot ota echo" });
        }, 1000)

        return () => clearInterval(c);
    })

    return (
        <div className={classes.app}>Hello world</div>
    )
}
