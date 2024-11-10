import { signOut } from "next-auth/react";
import Link from "next/link";
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
      // {
      //   icon: <CiHome size={20} />,
      //   label: "Home",
      //   href: "/dashboard/admin",
      //   visible: ["admin", "player"],
      // },
      {
        icon: <CiTrophy size={20} />,
        label: "Tournament",
        href: "/dashboard/list/tournaments",
        visible: ["admin", "player"],
      },
      {
        icon: <CiMaximize2 size={20} />,
        label: "Match",
        href: "/dashboard/list/matches",
        visible: ["admin", "player"],
      },
      {
        icon: <CiBullhorn size={20} />,
        label: "Leaderboard",
        href: "/dashboard/leaderboard",
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
        href: "/dashboard/profile",
      },
    ],
  },
];

const Menu = () => {
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
      {menuItemsOthers.map((i) => (
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
          <Link
            href="/"
            key="logout"
            onClick={() => signOut()}
            className="flex items-center justify-center lg:justify-start gap-4 text-gray-500 py-2"
          >
            <CiLogout size={20} />
            <span className="hidden lg:block">Logout</span>
          </Link>
        </div>
      ))}
    </div>
  );
};

export default Menu;
