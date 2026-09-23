import classes from "./App.module.css";
// import { SlingshotUpdater } from "slingshot-updater";

export default function App() {
    // useEffect(() => {
    //     const c = setInterval(() => { 
    //         SlingshotUpdater.echo({ value: "slingshot ota echo" });
    //     }, 1000)

    //     return () => clearInterval(c);
    // })

    return (
        <div className={classes.app}>
            <div className={classes.content}>Slingshot Updater Test App</div>
        </div>
    )
}
