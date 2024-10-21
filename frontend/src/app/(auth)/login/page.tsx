"use client";

import React from "react";
import { useRouter } from "next/navigation";
import LoginForm from "@/components/LoginForm";

export default function Login() {
	const router = useRouter();

	return (
		<div className="flex flex-col justify-between align-middle w-full py-12 px-14">
			<div className="flex justify-end items-end text-sm">
				Don&apos;t have an account?
				<span
					className="pl-1 underline cursor-pointer"
					onClick={() => router.push("/register")}
				>
					Sign Up
				</span>
			</div>
			<div className="flex flex-col justify-center align-middle gap-8">
				<h3 className="font-bold text-2xl">Sign in</h3>
				<LoginForm />
			</div>
			<div />
		</div>
	);
}
