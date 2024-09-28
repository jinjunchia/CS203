import React from "react";
import {
	Card,
	CardContent,
	CardDescription,
	CardFooter,
	CardHeader,
	CardTitle,
} from "@/components/ui/card";
import { Badge } from "./ui/badge";

const TournamentCard = () => {
	return (
		<div className="bg-red-300 bg-[url('/persongame.png')] h-80 bg-cover bg-center w-1/3 rounded-3xl p-6 flex flex-col justify-between">
			<div>
				<Badge>Aug 23 - Aug 24</Badge>
			</div>
			<div>Hello</div>
		</div>
	);
};

export default TournamentCard;
