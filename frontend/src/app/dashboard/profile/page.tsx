"use client";

import CountChart from "@/components/CountChart";
import UserCard from "@/components/UserCard";
import { useSession } from "next-auth/react";
import { useRouter } from "next/navigation";
import { useEffect } from "react";

const AdminPage = () => {
  const { data: session, status } = useSession();
  const router = useRouter();

  console.log((session?.user as any)?.user.id);
  useEffect(() => {
    router.replace("/dashboard/list/users/" + (session?.user as any)?.user.id);
  }, [router]);

  return null;
};

export default AdminPage;
