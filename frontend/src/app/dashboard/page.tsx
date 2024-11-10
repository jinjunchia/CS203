"use client";

import { useSession } from "next-auth/react";
import { useRouter } from "next/navigation";
import { useEffect } from "react";

const Page = () => {
  const { data: session, status } = useSession();
  const router = useRouter();

  console.log((session?.user as any)?.user.id);
  useEffect(() => {
    if ((session as any)?.user.user.userType === "ROLE_ADMIN") {
      router.replace("/dashboard/list/tournaments");
    } else {
      router.replace(
        "/dashboard/list/users/" + (session?.user as any)?.user.id
      );
    }
  }, [router]);

  return null;
};

export default Page;
