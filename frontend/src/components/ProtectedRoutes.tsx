"use client";

import { useSession } from "next-auth/react";
import { redirect, useRouter } from "next/navigation";
import { useEffect } from "react";

const ProtectedRoute = ({ children }: { children: React.ReactNode }) => {
	const { data: session, status } = useSession();
	const router = useRouter();

	useEffect(() => {
		console.log(session);
		if (status === "loading") return;
		if (!session) redirect("/login");
	}, [session, status, router]);

	if (status === "loading") {
		return "Loading";
	}

	return <>{children}</>;
};

export default ProtectedRoute;
