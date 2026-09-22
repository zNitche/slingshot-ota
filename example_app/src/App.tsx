import { useEffect } from "react";
import classes from "./App.module.css";
import { Slingshot } from "slingshot-ota";

export default function App() {
    useEffect(() => {
        const c = setInterval(() => { 
            Slingshot.echo({ value: "slingshot ota echo" });
        }, 1000)

        return () => clearInterval(c);
    })

    return (
        <div className={classes.app}>Hello world</div>
    )
}
