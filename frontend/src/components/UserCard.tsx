import React from "react";
import { Badge } from "./ui/badge";
import { IoIosMore } from "react-icons/io";

const UserCard = ({ type }: { type: string }) => {
	return (
		<div className="rounded-2xl odd:bg-lamaPurple even:bg-lamaYellow p-4 flex-1 min-w-[175px]">
			<div className="flex justify-between items-center">
				<Badge
					variant="secondary"
					className="text-[10px] text-green-600 px-2 py-1 "
				>
					2024/25
				</Badge>
				<IoIosMore size={25} />
			</div>
			<h1 className="text-2xl font-semibold my-4">1,234</h1>
			<h2 className="capitalize text-sm font-medium text-gray-500">{type}s</h2>
		</div>
	);
};

export default UserCard;
