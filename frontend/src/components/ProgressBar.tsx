import React from "react";
import { Progress } from "@/components/ui/progress";

const ProgressBar = () => {
	const [progress, setProgress] = React.useState(13);

	React.useEffect(() => {
		const timer = setTimeout(() => setProgress(66), 500);
		return () => clearTimeout(timer);
	}, []);

	return (
		<div className="w-full h-screen justify-center align-middle items-center">
			<Progress value={progress} className="w-[60%]" />
		</div>
	);
};

export default ProgressBar;
