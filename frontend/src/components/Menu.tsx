import Link from "next/link";
import React from "react";
import {
	CiBullhorn,
	CiHome,
	CiLogout,
	CiMaximize2,
	CiTrophy,
	CiUser,
} from "react-icons/ci";

const menuItems = [
	{
		title: "MENU",
		items: [
			{
				icon: <CiHome size={20} />,
				label: "Home",
				href: "/admin",
				visible: ["admin", "player"],
			},
			{
				icon: <CiTrophy size={20} />,
				label: "Tournament",
				href: "/tournaments",
				visible: ["admin", "player"],
			},
			{
				icon: <CiMaximize2 size={20} />,
				label: "Match",
				href: "/matches",
				visible: ["admin", "player"],
			},
			{
				icon: <CiBullhorn size={20} />,
				label: "Announcement",
				href: "/announcement",
				visible: ["admin", "player"],
			},
		],
	},
];

const menuItemsOthers = [
	{
		title: "OTHER",
		items: [
			{
				icon: <CiUser size={20} />,
				label: "Profile",
				href: "/profile",
				visible: ["admin", "player"],
			},
			{
				icon: <CiLogout size={20} />,
				label: "Logout",
				href: "/logout",
				visible: ["admin", "player"],
			},
		],
	},
];

const Menu = ({ makeOthersVisible = true }: { makeOthersVisible: boolean }) => {
	return (
		<div className="mt-4 text-sm">
			{menuItems.map((i) => (
				<div className="flex flex-col gap-2" key={i.title}>
					<span className="hidden lg:block text-gray-400 font-light my-4">
						{i.title}
					</span>
					{i.items.map((item) => (
						<Link
							href={item.href}
							key={item.label}
							className="flex items-center justify-center lg:justify-start gap-4 text-gray-500 py-2"
						>
							{item.icon}
							<span className="hidden lg:block">{item.label}</span>
						</Link>
					))}
				</div>
			))}
			{/* ONLY FOR LOGIN */}
			{makeOthersVisible &&
				menuItemsOthers.map((i) => (
					<div className="flex flex-col gap-2" key={i.title}>
						<span className="hidden lg:block text-gray-400 font-light my-4">
							{i.title}
						</span>
						{i.items.map((item) => (
							<Link
								href={item.href}
								key={item.label}
								className="flex items-center justify-center lg:justify-start gap-4 text-gray-500 py-2"
							>
								{item.icon}
								<span className="hidden lg:block">{item.label}</span>
							</Link>
						))}
					</div>
				))}
		</div>
	);
};

export default Menu;
