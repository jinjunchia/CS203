"use client";

import React, { createContext, useState, useEffect, ReactNode } from "react";
import axiosInstance from "@/lib/axios";
import { useToast } from "@/hooks/use-toast";
import { ToastAction } from "@/components/ui/toast";

export const AuthContext = createContext<AuthContextType>({
	user: null,
	loading: true,
	login: async () => {},
	logout: () => {},
});

export const AuthProvider = ({ children }: { children: ReactNode }) => {
	const { toast } = useToast();
	const [user, setUser] = useState<User | null>(null);
	const [loading, setLoading] = useState<boolean>(true);

	const fetchUser = async () => {
		try {
			const response = await axiosInstance.get("/auth/me");
			setUser(response.data.user);
		} catch (error) {
			setUser(null);
			localStorage.removeItem("token");
			delete axiosInstance.defaults.headers.common["Authorization"];
		} finally {
			setLoading(false);
		}
	};

	useEffect(() => {
		const token = localStorage.getItem("token");
		if (token) {
			axiosInstance.defaults.headers.common[
				"Authorization"
			] = `Bearer ${token}`;
			fetchUser();
		} else {
			setLoading(false);
		}
	}, []);

	const login = async (username: string, password: string) => {
		console.log("AuthContext login called with:", { username, password });
		try {
			const response = await axiosInstance.post("/auth/login", {
				username,
				password,
			});
			const { jwt, user } = response.data;
			localStorage.setItem("token", jwt);
			axiosInstance.defaults.headers.common["Authorization"] = `Bearer ${jwt}`;
			setUser(user);
			// eslint-disable-next-line @typescript-eslint/no-explicit-any
		} catch (error: any) {
			toast({
				variant: "destructive",
				title: "Uh oh! Something went wrong.",
				description: "There was a problem with your credentials.",
				action: <ToastAction altText="Try again">Try again</ToastAction>,
			});
		}
	};

	const logout = () => {
		localStorage.removeItem("token");
		delete axiosInstance.defaults.headers.common["Authorization"];
		setUser(null);
	};

	return (
		<AuthContext.Provider value={{ user, loading, login, logout }}>
			{children}
		</AuthContext.Provider>
	);
};
