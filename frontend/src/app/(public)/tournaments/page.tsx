import TournamentCard from "@/components/TournamentCard";
import { Button } from "@/components/ui/button";
import React from "react";

const TournamentPage = () => {
	return (
		<div className="pt-4">
			<div className="flex flex-col justify-between gap-3 mb-8">
				<h1 className="text-3xl font-bold">Featured Tournaments</h1>
				<p>Join the fray in your favorite game and claim glory!</p>
			</div>
			<div className="flex gap-12">
				<TournamentCard />
				<TournamentCard />
				<TournamentCard />
				<TournamentCard />
			</div>
		</div>
	);
};

export default TournamentPage;
