import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";
import { Toaster } from "@/components/ui/toaster";
import Provider from "@/components/Provider";

export const metadata: Metadata = {
	title: "TournaX",
	description: "Elevate your tournaments with TournaX",
};

const inter = Inter({ subsets: ["latin"] });

export default function RootLayout({
	children,
}: Readonly<{
	children: React.ReactNode;
}>) {
	return (
		<html lang="en">
			<body className={inter.className}>
				<Provider>{children}</Provider>
				<Toaster />
			</body>
		</html>
	);
}
