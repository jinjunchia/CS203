"use client";

import Image from "next/image";
import Link from "next/link";
import React from "react";
import { Button } from "./ui/button";

const PublicNavbar = () => {
	return (
		<nav className="mx-auto flex justify-between items-center py-6">
			<div className="flex">
				<Image src="logo.svg" alt="logo" height={35} width={35} />
			</div>
			<div className="flex justify-between items-center gap-5">
				<Link
					href="/tournaments"
					className="bg-white rounded-full flex items-center justify-center cursor-pointer relative"
				>
					Tournaments
				</Link>
				<Link
					href="/rankings"
					className="bg-white rounded-full flex items-center justify-center cursor-pointer relative"
				>
					Rankings
				</Link>
				<Link
					href="/announcement"
					className="bg-white rounded-full flex items-center justify-center cursor-pointer relative"
				>
					Announcement
				</Link>
			</div>
			<div>
				<Link href="/login">
					<Button className="rounded-full bg-gradient-to-r from-blue-500 to-green-500 text-white font-bold">
						Get Started
					</Button>
				</Link>
			</div>
		</nav>
	);
};

export default PublicNavbar;
