"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";
import InputField from "../InputField";

const schema = z.object({
  name: z
    .string()
    .min(3, { message: "Username must be at least 3 characters long!" })
    .max(20, { message: "Username must be at most 20 characters long!" }),
  email: z.string().email({ message: "Invalid email address!" }),
  minEloRating: z.string().email({ message: "Invalid email address!" }),
  maxEloRating: z.string().email({ message: "Invalid email address!" }),
});

type Inputs = z.infer<typeof schema>;

const TournamentForm = ({
  type,
  data,
}: {
  type: "create" | "update";
  data?: any;
}) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<Inputs>({
    resolver: zodResolver(schema),
  });

  const onSubmit = handleSubmit((data) => {
    console.log(data);
  });

  return (
    <form className="flex flex-col gap-8" onSubmit={onSubmit}>
      <h1 className="text-xl font-semibold">Create tournament</h1>
      <div className="flex justify-between flex-wrap gap-4">
        <InputField
          label="Name"
          name="name"
          defaultValue={data?.name}
          register={register}
          error={errors?.name}
        />
        <InputField
          label="Date"
          name="date"
          defaultValue={data?.startDate}
          register={register}
          error={errors?.email}
        />
      </div>
      <InputField
        label="Max Elo"
        name="date"
        defaultValue={data?.minEloRating}
        register={register}
        error={errors?.minEloRating}
      />
      <InputField
        label="Min Elo"
        name="date"
        defaultValue={data?.maxEloRating}
        register={register}
        error={errors?.maxEloRating}
      />
      <button className="bg-blue-400 text-white p-2 rounded-md">
        {type === "create" ? "Create" : "Update"}
      </button>
    </form>
  );
};

export default TournamentForm;
